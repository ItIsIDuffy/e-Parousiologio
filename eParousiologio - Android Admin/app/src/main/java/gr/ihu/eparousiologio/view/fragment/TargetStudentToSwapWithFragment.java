package gr.ihu.eparousiologio.view.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.adapter.SwapStudentAdapter;
import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.repository.StudentRecordsRepositoryDAO;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.OnResultListener;
import gr.ihu.eparousiologio.view.MainActivity;


public class TargetStudentToSwapWithFragment extends Fragment {

    private static final String ARG_COURSE = "course";
    private static final String ARG_SECTION = "section";
    private static final String ARG_SECTION_SOURCE_SECTION = "sourceSection";
    private static final String ARG_SECTION_STUDENT1_TO_BE_SWAPPED = "student1ToBeSwapped";

    View rootView;
    StudentRecordsRepositoryDAO studentRecordsRepositoryDAO = new StudentRecordsRepositoryDAO();
    ListenerRegistration studentsListener = null;
    SwapStudentAdapter swapStudentRecordAdapter;
    RecyclerView viewStudentsListRV;
    MaterialTextView
            studentCourseSectionListMTV;
    Section sourceSection, targetSection;
    Course course;
    private Student student1ToBeSwapped;

    public TargetStudentToSwapWithFragment() {
        // Required empty public constructor
    }

    public static TargetStudentToSwapWithFragment newInstance(Course course, Section targetSection, Section sourceSection, Student student1ToBeSwapped) {
        TargetStudentToSwapWithFragment fragment = new TargetStudentToSwapWithFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_COURSE, course);
        args.putParcelable(ARG_SECTION, targetSection);
        args.putParcelable(ARG_SECTION_SOURCE_SECTION, sourceSection);
        args.putParcelable(ARG_SECTION_STUDENT1_TO_BE_SWAPPED, student1ToBeSwapped);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            course = getArguments().getParcelable(ARG_COURSE);
            targetSection = getArguments().getParcelable(ARG_SECTION);
            sourceSection = getArguments().getParcelable(ARG_SECTION_SOURCE_SECTION);
            student1ToBeSwapped = getArguments().getParcelable(ARG_SECTION_STUDENT1_TO_BE_SWAPPED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_target_student_to_swap_from_list, container, false);
        initializeUI();
        studentCourseSectionListMTV.setText(MessageFormat.format("Μάθημα {0}\nΤμήμα {1}", course.getTitle(), sourceSection.getName()));

        return rootView;
    }

    private void initializeUI() {
        viewStudentsListRV = rootView.findViewById(R.id.swapStudentsListRV);
        studentCourseSectionListMTV = rootView.findViewById(R.id.swapStudentCourseSectionListMTV);

        swapStudentRecordAdapter = new SwapStudentAdapter(new ArrayList<>(), requireContext(), student2ToBeSwapped -> {
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.swap_users_popup_verify, null);

            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
            MaterialTextView swapStudentsTitleMessageMTV = dialogView.findViewById(R.id.swapStudentsTitleMessageMTV);
            swapStudentsTitleMessageMTV.setText(getString(R.string.swap_students_title_message, student1ToBeSwapped.getFullName() + " "
                    + student1ToBeSwapped.getStudentAEM(), sourceSection.getName(), student2ToBeSwapped.getFullName() + " "
                    + student2ToBeSwapped.getStudentAEM(), targetSection.getName()));

            dialogView.findViewById(R.id.swapStudentsCancelMB).setOnClickListener(v -> {
                dialog.dismiss();
            });

            dialogView.findViewById(R.id.swapStudentsAcceptMB).setOnClickListener(v -> {
                studentRecordsRepositoryDAO.swapStudents(course.getCourseId(), sourceSection.getLabId(), student1ToBeSwapped,
                        targetSection.getLabId(), student2ToBeSwapped, new OnResultListener<>() {
                            @Override
                            public void onSuccess(Void result) {
                                CustomToast.showSuccess(requireActivity(), "Η αμοιβαία ανταλλαγή ολοκληρώθηκε");
                                dialog.dismiss();
                                ((MainActivity) requireActivity()).resetToMainMenu();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                CustomToast.showError(requireActivity(), e.getMessage());
                            }
                        });
            });

            dialog.show();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(
                        (int) (requireActivity().getResources().getDisplayMetrics().widthPixels * 0.8),
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
            }
        });

        viewStudentsListRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
        viewStudentsListRV.setAdapter(swapStudentRecordAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (swapStudentRecordAdapter == null) {
            initializeUI();
        }

        studentsListener = studentRecordsRepositoryDAO.listenToStudentsInSection(course.getCourseId(), targetSection.getLabId(), new OnResultListener<>() {
            @Override
            public void onSuccess(List<Student> resultStudents) {
                swapStudentRecordAdapter.setStudents(resultStudents);
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                CustomToast.showError(requireActivity(), e.getMessage());
            }
        });
    }

    @Override
    public void onStop() {
        if (studentsListener != null) {
            studentsListener.remove();
            studentsListener = null;
        }
        super.onStop();
    }
}