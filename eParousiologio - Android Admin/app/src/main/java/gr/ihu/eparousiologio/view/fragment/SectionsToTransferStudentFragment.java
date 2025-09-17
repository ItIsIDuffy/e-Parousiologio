package gr.ihu.eparousiologio.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.adapter.CourseSectionAdapter;
import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.repository.CourseSectionDAO;
import gr.ihu.eparousiologio.repository.StudentRecordsRepositoryDAO;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.OnResultListener;
import gr.ihu.eparousiologio.view.MainActivity;

public class SectionsToTransferStudentFragment extends Fragment {
    private static final String ARG_FRAGMENT_COURSE = "course";
    private static final String ARG_FRAGMENT_SECTION_SOURCE = "sectionSource";
    private static final String ARG_FRAGMENT_STUDENT_DESTINATION = "studentToTransfer";
    private final CourseSectionDAO courseSectionDAO = new CourseSectionDAO();
    private final StudentRecordsRepositoryDAO studentRecordsRepositoryDAO = new StudentRecordsRepositoryDAO();
    View rootView;
    private Student studentToTransfer;
    private CourseSectionAdapter courseSectionAdapter;
    private List<Course> transferStudentCourseSectionsList;
    private HashMap<Course, List<Section>> listDataChild;
    private Course course;
    private Section sectionSource;

    public SectionsToTransferStudentFragment() {
    }

    public static SectionsToTransferStudentFragment newInstance(Course course, Section sectionSource, Student studentToTransfer) {
        SectionsToTransferStudentFragment fragment = new SectionsToTransferStudentFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FRAGMENT_COURSE, course);
        args.putParcelable(ARG_FRAGMENT_SECTION_SOURCE, sectionSource);
        args.putParcelable(ARG_FRAGMENT_STUDENT_DESTINATION, studentToTransfer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            course = getArguments().getParcelable(ARG_FRAGMENT_COURSE);
            sectionSource = getArguments().getParcelable(ARG_FRAGMENT_SECTION_SOURCE);
            studentToTransfer = getArguments().getParcelable(ARG_FRAGMENT_STUDENT_DESTINATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sections_to_transfer_student, container, false);

        ExpandableListView transferStudentELV = rootView.findViewById(R.id.transferStudentELV);

        transferStudentCourseSectionsList = new ArrayList<>();
        listDataChild = new HashMap<>();
        courseSectionAdapter = new CourseSectionAdapter(requireContext(), transferStudentCourseSectionsList, listDataChild);
        transferStudentELV.setAdapter(courseSectionAdapter);

        loadData(course.getCourseId());

        transferStudentELV.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Course selectedCourse = (Course) courseSectionAdapter.getGroup(groupPosition);
            Section selectedSection = (Section) courseSectionAdapter.getChild(groupPosition, childPosition);

            studentRecordsRepositoryDAO.transferStudent(selectedCourse.getCourseId(), sectionSource.getLabId(), selectedSection.getLabId(), studentToTransfer, new OnResultListener<>() {
                @Override
                public void onSuccess(Void result) {
                    CustomToast.showSuccess(requireActivity(), "Η μεταφορά του " + studentToTransfer.getStudentAEM() + " στο τμήμα " + selectedSection.getName() + " πραγματοποιήθηκε");
                    ((MainActivity) requireActivity()).resetToMainMenu();
                }

                @Override
                public void onFailure(Exception e) {
                    CustomToast.showError(requireActivity(), e.getMessage());
                }
            });

//            ((MainActivity) requireActivity())
//                    .addFragment(AccessStudentsRecordListFragment.newInstance(selectedCourse.getCourseId(), selectedSection.getLabId(), selectedCourse.getTitle(), selectedSection.getName()));

            return true;
        });

        return rootView;
    }

    private void loadData(String courseId) {
        courseSectionDAO.getCourseById(courseId, new OnResultListener<>() {
            @Override
            public void onSuccess(Course course) {
                transferStudentCourseSectionsList.clear();
                listDataChild.clear();
                transferStudentCourseSectionsList.add(course);

                courseSectionDAO.getAllSectionsByCourseId(course.getCourseId(), new OnResultListener<>() {
                    @Override
                    public void onSuccess(List<Section> sections) {

                        List<Section> cleanSections = new ArrayList<>();

                        for (Section section : sections) {
                            if (section.isUnassigned()) {
                                continue;
                            }

                            if (section.getLabId().equals(sectionSource.getLabId())) {
                                continue;
                            }

                            cleanSections.add(section);
                        }

                        listDataChild.put(course, cleanSections);
                        courseSectionAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        CustomToast.showError(requireActivity(), "Σφάλμα ΕΡΓΑΣΤΗΡΙΑ: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                CustomToast.showError(requireActivity(), "Σφάλμα ΜΑΘΗΜΑΤΑ: " + e.getMessage());
            }
        });
    }
}