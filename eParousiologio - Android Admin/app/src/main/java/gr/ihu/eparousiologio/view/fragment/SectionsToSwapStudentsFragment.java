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

public class SectionsToSwapStudentsFragment extends Fragment {
    private static final String ARG_FRAGMENT_COURSE = "course";
    private static final String ARG_FRAGMENT_SECTION = "section";
    private static final String ARG_FRAGMENT_STUDENT_TO_SWAP = "studentToSwap";
    private final CourseSectionDAO courseSectionDAO = new CourseSectionDAO();
    private final StudentRecordsRepositoryDAO studentRecordsRepositoryDAO = new StudentRecordsRepositoryDAO();
    View rootView;
    private Student studentToSwap;
    private CourseSectionAdapter courseSectionAdapter;
    private List<Course> swapStudentCourseSectionsList;
    private HashMap<Course, List<Section>> listDataChild;
    private Course course;
    private Section sourceSection;

    public SectionsToSwapStudentsFragment() {
        // Required empty public constructor
    }

    public static SectionsToSwapStudentsFragment newInstance(Course course, Section section, Student studentToSwap) {
        SectionsToSwapStudentsFragment fragment = new SectionsToSwapStudentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FRAGMENT_COURSE, course);
        args.putParcelable(ARG_FRAGMENT_SECTION, section);
        args.putParcelable(ARG_FRAGMENT_STUDENT_TO_SWAP, studentToSwap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            course = getArguments().getParcelable(ARG_FRAGMENT_COURSE);
            sourceSection = getArguments().getParcelable(ARG_FRAGMENT_SECTION);
            studentToSwap = getArguments().getParcelable(ARG_FRAGMENT_STUDENT_TO_SWAP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_sections_to_swap_student, container, false);

        ExpandableListView swapStudentELV = rootView.findViewById(R.id.swapStudentELV);

        swapStudentCourseSectionsList = new ArrayList<>();
        listDataChild = new HashMap<>();
        courseSectionAdapter = new CourseSectionAdapter(requireContext(), swapStudentCourseSectionsList, listDataChild);
        swapStudentELV.setAdapter(courseSectionAdapter);

        loadData(course.getCourseId());

        swapStudentELV.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Course targetSelectedCourse = (Course) courseSectionAdapter.getGroup(groupPosition);
            Section targetSelectedSection = (Section) courseSectionAdapter.getChild(groupPosition, childPosition);

            ((MainActivity) requireActivity())
                    .addFragment(TargetStudentToSwapWithFragment.newInstance(targetSelectedCourse, targetSelectedSection, sourceSection, studentToSwap));

            return true;
        });

        return rootView;
    }

    private void loadData(String courseId) {
        courseSectionDAO.getCourseById(courseId, new OnResultListener<>() {
            @Override
            public void onSuccess(Course course) {
                swapStudentCourseSectionsList.clear();
                listDataChild.clear();
                swapStudentCourseSectionsList.add(course);

                courseSectionDAO.getAllSectionsByCourseId(course.getCourseId(), new OnResultListener<>() {
                    @Override
                    public void onSuccess(List<Section> sections) {

                        List<Section> cleanSections = new ArrayList<>();

                        for (Section section : sections) {
                            if (section.isUnassigned()) {
                                continue;
                            }

                            if (section.getLabId().equals(sourceSection.getLabId())) {
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