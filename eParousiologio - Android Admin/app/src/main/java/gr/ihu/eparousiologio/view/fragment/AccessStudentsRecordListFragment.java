package gr.ihu.eparousiologio.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.adapter.AccessStudentRecordAdapter;
import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.repository.StudentRecordsRepositoryDAO;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.OnResultListener;
import gr.ihu.eparousiologio.view.MainActivity;
import gr.ihu.eparousiologio.view.helper.AccessStudentRecordActionsListener;

public class AccessStudentsRecordListFragment extends Fragment {

    private static final String ARG_COURSE = "course";
    private static final String ARG_SECTION = "section";

    View rootView;
    StudentRecordsRepositoryDAO studentRecordsRepositoryDAO = new StudentRecordsRepositoryDAO();
    ListenerRegistration studentsListener = null;
    AccessStudentRecordAdapter accessStudentRecordAdapter;
    RecyclerView viewStudentsListRV;
    MaterialTextView
            studentCourseSectionListMTV;
    MaterialButton addStudentEFAB;
    Section section;
    Course course;

    public AccessStudentsRecordListFragment() {
        // Required empty public constructor
    }

    public static AccessStudentsRecordListFragment newInstance(Course course, Section section) {
        AccessStudentsRecordListFragment fragment = new AccessStudentsRecordListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_COURSE, course);
        args.putParcelable(ARG_SECTION, section);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            course = getArguments().getParcelable(ARG_COURSE);
            section = getArguments().getParcelable(ARG_SECTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_access_student_record_list, container, false);

        initializeUI();

        studentCourseSectionListMTV.setText(MessageFormat.format("Μάθημα {0}\nΤμήμα {1}", course.getTitle(), section.getName()));

        return rootView;
    }

    private void initializeUI() {
        viewStudentsListRV = rootView.findViewById(R.id.accessStudentsListRV);
        studentCourseSectionListMTV = rootView.findViewById(R.id.accessStudentCourseSectionListMTV);

        accessStudentRecordAdapter = new AccessStudentRecordAdapter(section.isUnassigned(), new ArrayList<>(), requireContext(), new AccessStudentRecordActionsListener() {
            @Override
            public void onTransfer(Student student) {
                ((MainActivity) requireActivity()).addFragment(SectionsToTransferStudentFragment.newInstance(course, section, student));
            }

            @Override
            public void onSwap(Student student) {
                ((MainActivity) requireActivity()).addFragment(SectionsToSwapStudentsFragment.newInstance(course, section, student));
            }

            @Override
            public void onDelete(Student student) {
                studentRecordsRepositoryDAO.deleteStudentFromSection(course.getCourseId(), section.getLabId(), student, new OnResultListener<>() {
                    @Override
                    public void onSuccess(Void result) {
                        CustomToast.showSuccess(requireActivity(), "Επιτυχία διαγραφής");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        CustomToast.showError(requireActivity(), e.getMessage());
                    }
                });
            }
        });
        viewStudentsListRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
        viewStudentsListRV.setAdapter(accessStudentRecordAdapter);

        addStudentEFAB = rootView.findViewById(R.id.addStudentMB);

        if (section.isUnassigned()) {
            addStudentEFAB.setVisibility(View.GONE);
            return;
        }

        addStudentEFAB.setOnClickListener(view -> {
            AddStudentSheet addStudentSheet = new AddStudentSheet(course, section);
            addStudentSheet.show(getParentFragmentManager(), "AddStudentSheet");
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (accessStudentRecordAdapter == null) {
            initializeUI();
        }

        studentsListener = studentRecordsRepositoryDAO.listenToStudentsInSection(course.getCourseId(), section.getLabId(), new OnResultListener<>() {
            @Override
            public void onSuccess(List<Student> resultStudents) {
                accessStudentRecordAdapter.setStudents(resultStudents);
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