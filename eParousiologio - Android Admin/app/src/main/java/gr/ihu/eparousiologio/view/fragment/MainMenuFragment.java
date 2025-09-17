package gr.ihu.eparousiologio.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Objects;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.model.LiveCourse;
import gr.ihu.eparousiologio.repository.LiveCourseRepositoryDAO;
import gr.ihu.eparousiologio.util.AuthManager;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.FileReaderUtil;
import gr.ihu.eparousiologio.util.OnResultListener;
import gr.ihu.eparousiologio.view.MainActivity;

public class MainMenuFragment extends Fragment {

    Context context;
    LiveCourseRepositoryDAO liveCourseRepository = new LiveCourseRepositoryDAO();
    ListenerRegistration liveCourse = null;
    private View rootView;
    private MaterialButton accessStudentRecordMB, importStudentRecordMB, selectCurrentSectionMB, exportAttendanceMB, clearCourseMB;
    private MaterialTextView currentCourseMTV, currentSectionMTV;
    private MaterialSwitch isOpenSwitch;
    private ActivityResultLauncher<Intent> elabsTextFileLauncher;

    private ActivityResultLauncher<String> writePermLauncher;

    private boolean updatingFromFirestore = false;

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        elabsTextFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                ensureTeacherAndImport(uri);
            }
        });

        writePermLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                if (isAdded()) {
                    ((MainActivity) requireActivity()).addFragment(ChooseCourseSectionToExportExcelFragment.newInstance());
                }
            } else {
                if (isAdded()) {
                    CustomToast.showError(requireActivity(), "Η άδεια αποθήκευσης δεν δόθηκε. Η εξαγωγή σε Excel δεν μπορεί να συνεχιστεί.");
                }
            }
        });

        initializeUI();

        return rootView;
    }

    private void ensureTeacherAndImport(Uri uri) {
        AuthManager.get().signInTeacher("parousiologiodipae@gmail.com", "parousiologiodipae", () -> readTextFromUri(uri), e -> CustomToast.showError(requireActivity(), "Αποτυχία σύνδεσης καθηγητή: " + e.getMessage()));
    }

    private void initializeUI() {
        accessStudentRecordMB = rootView.findViewById(R.id.accessStudentRecordMB);
        importStudentRecordMB = rootView.findViewById(R.id.importStudentRecordMB);
        selectCurrentSectionMB = rootView.findViewById(R.id.selectCurrentSectionMB);
        exportAttendanceMB = rootView.findViewById(R.id.exportAttendanceMB);
        currentCourseMTV = rootView.findViewById(R.id.currentCourseMTV);
        currentSectionMTV = rootView.findViewById(R.id.currentSectionMTV);
        isOpenSwitch = rootView.findViewById(R.id.isOpenSwitch);
        clearCourseMB = rootView.findViewById(R.id.clearCourseMB);

        accessStudentRecordMB.setOnClickListener(view -> {
            if (isAdded()) {
                ((MainActivity) requireActivity()).addFragment(SelectCourseSectionToViewStudentsFragment.newInstance());
            }
        });

        importStudentRecordMB.setOnClickListener(view -> {
            if (isAdded()) {
                openFilePicker();
            }
        });

        selectCurrentSectionMB.setOnClickListener(view -> {
            if (isAdded()) {
                ((MainActivity) requireActivity()).addFragment(SelectLiveCourseSectionFragment.newInstance());
            }
        });

        exportAttendanceMB.setOnClickListener(view -> {
            if (!isAdded()) return;

            if (Build.VERSION.SDK_INT == 28) {
                int hasWrite = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (hasWrite == PackageManager.PERMISSION_GRANTED) {
                    ((MainActivity) requireActivity()).addFragment(ChooseCourseSectionToExportExcelFragment.newInstance());
                } else {
                    writePermLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            } else {
                ((MainActivity) requireActivity()).addFragment(ChooseCourseSectionToExportExcelFragment.newInstance());
            }
        });

        resetUIForNoCourse();
    }

    private void readTextFromUri(Uri uri) {
        if (context == null) return;
        new FileReaderUtil(context).readTextFromUri(uri);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        elabsTextFileLauncher.launch(intent);
    }

    @Override
    public void onStop() {
        if (liveCourse != null) {
            liveCourse.remove();
            liveCourse = null;
        }
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();

        liveCourse = liveCourseRepository.getLiveCourse(new OnResultListener<>() {
            @Override
            public void onSuccess(LiveCourse liveCourse) {
                if (!isAdded()) return;

                currentCourseMTV.setText(getString(R.string.current_course, liveCourse.getCourseTitle()));
                currentSectionMTV.setText(getString(R.string.current_section, liveCourse.getLabName()));

                clearCourseMB.setVisibility(View.VISIBLE);
                clearCourseMB.setOnClickListener(view -> {
                    liveCourseRepository.deleteLiveCourse(new OnResultListener<>() {
                        @Override
                        public void onSuccess(Void result) {
                            resetUIForNoCourse();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            CustomToast.showError(requireActivity(), e.getMessage());
                        }
                    });
                });

                isOpenSwitch.setOnCheckedChangeListener(null);
                updatingFromFirestore = true;
                isOpenSwitch.setVisibility(View.VISIBLE);
                isOpenSwitch.setEnabled(true);
                isOpenSwitch.setClickable(true);
                isOpenSwitch.setChecked(liveCourse.getIsOpen());
                updatingFromFirestore = false;

                isOpenSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    if (updatingFromFirestore) return;

                    liveCourse.setOpen(isChecked);
                    isOpenSwitch.setEnabled(false);
                    isOpenSwitch.setClickable(false);

                    liveCourseRepository.updateLiveCourseAttendance(liveCourse, new OnResultListener<>() {
                        @Override
                        public void onSuccess(Void result) {
                            isOpenSwitch.setEnabled(true);
                            isOpenSwitch.setClickable(true);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            isOpenSwitch.setEnabled(true);
                            isOpenSwitch.setClickable(true);
                            CustomToast.showError(requireActivity(), e.getMessage());
                        }
                    });
                });
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;

                if (Objects.equals(e.getMessage(), "Δεν υπάρχει ενεργό μάθημα.")) {
                    resetUIForNoCourse();
                } else {
                    CustomToast.showError(requireActivity(), e.getMessage());
                }
            }
        });
    }

    private void resetUIForNoCourse() {
        currentCourseMTV.setText(getString(R.string.current_course, "-"));
        currentSectionMTV.setText(getString(R.string.current_section, "-"));

        clearCourseMB.setVisibility(View.GONE);

        isOpenSwitch.setOnCheckedChangeListener(null);

        updatingFromFirestore = true;
        isOpenSwitch.setChecked(false);
        updatingFromFirestore = false;

        isOpenSwitch.setVisibility(View.GONE);
        isOpenSwitch.setEnabled(false);
        isOpenSwitch.setClickable(false);
    }
}