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
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
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

    private Context context;
    private LiveCourseRepositoryDAO liveCourseRepository = new LiveCourseRepositoryDAO();
    private ListenerRegistration liveCourse = null;
    private Group actionsGroup;
    private View rootView;

    private MaterialButton accessStudentRecordMB,
            importStudentRecordMB,
            selectCurrentSectionMB,
            exportAttendanceMB,
            addNoteToSectionMB,
            saveAttendanceForTodayMB,
            clearCourseMB;

    private MaterialTextView currentCourseMTV, currentSectionMTV;
    private ActivityResultLauncher<Intent> elabsTextFileLauncher;
    private ActivityResultLauncher<String> writePermLauncher;

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

        // file picker launcher
        elabsTextFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        importStudentRecord(uri);
                    }
                });

        // permission launcher (for Excel export)
        writePermLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (!isAdded()) return;

                    if (granted) {
                        ((MainActivity) requireActivity())
                                .addFragment(ChooseCourseSectionToExportExcelFragment.newInstance());
                    } else {
                        CustomToast.showError(requireActivity(),
                                "Η άδεια αποθήκευσης δεν δόθηκε. Η εξαγωγή σε Excel δεν μπορεί να συνεχιστεί.");
                    }
                });

        initializeUI();

        return rootView;
    }

    private void initializeUI() {
        actionsGroup = rootView.findViewById(R.id.actionsGroup);
        accessStudentRecordMB = rootView.findViewById(R.id.accessStudentRecordMB);
        importStudentRecordMB = rootView.findViewById(R.id.importStudentRecordMB);
        selectCurrentSectionMB = rootView.findViewById(R.id.selectCurrentSectionMB);
        exportAttendanceMB = rootView.findViewById(R.id.exportAttendanceMB);
        currentCourseMTV = rootView.findViewById(R.id.currentCourseMTV);
        currentSectionMTV = rootView.findViewById(R.id.currentSectionMTV);
        saveAttendanceForTodayMB = rootView.findViewById(R.id.saveAttendanceForTodayMB);
        addNoteToSectionMB = rootView.findViewById(R.id.addNoteToSectionMB);
        clearCourseMB = rootView.findViewById(R.id.clearCourseMB);

        // Προβολή μαθημάτων
        accessStudentRecordMB.setOnClickListener(v ->
                ((MainActivity) requireActivity())
                        .addFragment(SelectCourseSectionToViewStudentsFragment.newInstance())
        );

        // Εισαγωγή αρχείου φοιτητών
        importStudentRecordMB.setOnClickListener(v ->
                AuthManager.get().ensureTeacherSignedIn(this::openFilePicker,
                        e -> CustomToast.showError(requireActivity(),
                                "Αποτυχία σύνδεσης: " + e.getMessage()))
        );

        // Επιλογή ενεργού τμήματος
        selectCurrentSectionMB.setOnClickListener(v ->
                ((MainActivity) requireActivity())
                        .addFragment(SelectLiveCourseSectionFragment.newInstance())
        );

        // Εξαγωγή παρουσιών σε Excel
        exportAttendanceMB.setOnClickListener(v -> {
            if (!isAdded()) return;

            AuthManager.get().ensureTeacherSignedIn(() -> {
                if (Build.VERSION.SDK_INT == 28) {
                    int hasWrite = ContextCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasWrite == PackageManager.PERMISSION_GRANTED) {
                        ((MainActivity) requireActivity())
                                .addFragment(ChooseCourseSectionToExportExcelFragment.newInstance());
                    } else {
                        writePermLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                } else {
                    ((MainActivity) requireActivity())
                            .addFragment(ChooseCourseSectionToExportExcelFragment.newInstance());
                }
            }, e -> CustomToast.showError(requireActivity(),
                    "Αποτυχία επαλήθευσης: " + e.getMessage()));
        });

        resetUIForNoCourse();
    }

    // Διαβάζει αρχείο κειμένου φοιτητών
    private void importStudentRecord(Uri uri) {
        if (context == null) return;

        AuthManager.get().ensureTeacherSignedIn(() ->
                        new FileReaderUtil(context).readTextFromUri(uri),
                e -> CustomToast.showError(requireActivity(),
                        "Αποτυχία σύνδεσης καθηγητή: " + e.getMessage()));
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

        AuthManager.get().ensureTeacherSignedIn(() -> {

            liveCourse = liveCourseRepository.getLiveCourse(new OnResultListener<>() {
                @Override
                public void onSuccess(LiveCourse liveCourse) {
                    if (!isAdded()) return;

                    currentCourseMTV.setText(getString(R.string.current_course, liveCourse.getCourseTitle()));
                    currentSectionMTV.setText(getString(R.string.current_section, liveCourse.getLabName()));

                    actionsGroup.setVisibility(View.VISIBLE);

                    saveAttendanceForTodayMB.setOnClickListener(v ->
                            AuthManager.get().ensureTeacherSignedIn(() ->
                                    ((MainActivity) requireActivity())
                                            .addFragment(DailyAttendanceRecordFragment.newInstance(liveCourse)))
                    );

                    addNoteToSectionMB.setOnClickListener(v ->
                            AuthManager.get().ensureTeacherSignedIn(() -> {
                                AddSectionNoteSheet addSectionNoteSheet = new AddSectionNoteSheet(liveCourse);
                                addSectionNoteSheet.show(getParentFragmentManager(), "AddSectionNoteSheet");
                            })
                    );

                    clearCourseMB.setOnClickListener(v ->
                            AuthManager.get().ensureTeacherSignedIn(() ->
                                    liveCourseRepository.deleteLiveCourse(new OnResultListener<>() {
                                        @Override
                                        public void onSuccess(Void result) {
                                            resetUIForNoCourse();
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            CustomToast.showError(requireActivity(), e.getMessage());
                                        }
                                    })
                            )
                    );
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

        }, e -> {
            if (isAdded()) {
                CustomToast.showError(requireActivity(),
                        "Αποτυχία σύνδεσης κατά την εκκίνηση: " + e.getMessage());
            }
        });
    }


    private void resetUIForNoCourse() {
        currentCourseMTV.setText(getString(R.string.current_course, "-"));
        currentSectionMTV.setText(getString(R.string.current_section, "-"));
        actionsGroup.setVisibility(View.GONE);
    }
}
