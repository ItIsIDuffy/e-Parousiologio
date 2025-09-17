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
import gr.ihu.eparousiologio.repository.CourseSectionDAO;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.OnResultListener;
import gr.ihu.eparousiologio.view.MainActivity;

public class SelectCourseSectionToViewStudentsFragment extends Fragment {

    private final CourseSectionDAO courseSectionDAO = new CourseSectionDAO();
    View rootView;
    private CourseSectionAdapter courseSectionAdapter;
    private List<Course> courseSectionsList;
    private HashMap<Course, List<Section>> listDataChild;
    public SelectCourseSectionToViewStudentsFragment() {
        // Required empty public constructor
    }

    public static SelectCourseSectionToViewStudentsFragment newInstance() {
        SelectCourseSectionToViewStudentsFragment fragment = new SelectCourseSectionToViewStudentsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_select_course_section_to_view_students, container, false);
        ExpandableListView courseSectionToViewStudentsELV = rootView.findViewById(R.id.courseSectionToViewStudentsELV);

        courseSectionsList = new ArrayList<>();
        listDataChild = new HashMap<>();
        courseSectionAdapter = new CourseSectionAdapter(requireContext(), courseSectionsList, listDataChild);
        courseSectionToViewStudentsELV.setAdapter(courseSectionAdapter);

        loadData();

        courseSectionToViewStudentsELV.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Course selectedCourse = (Course) courseSectionAdapter.getGroup(groupPosition);
            Section selectedSection = (Section) courseSectionAdapter.getChild(groupPosition, childPosition);

            ((MainActivity) requireActivity()).addFragment(AccessStudentsRecordListFragment.newInstance(selectedCourse, selectedSection));

            return true;
        });


        return rootView;
    }

    private void loadData() {
        courseSectionDAO.getAllCourses(new OnResultListener<>() {
            @Override
            public void onSuccess(List<Course> courses) {
                courseSectionsList.clear();
                listDataChild.clear();

                for (Course course : courses) {
                    courseSectionsList.add(course);

                    courseSectionDAO.getAllSectionsByCourseId(course.getCourseId(), new OnResultListener<>() {
                        @Override
                        public void onSuccess(List<Section> sections) {
                            listDataChild.put(course, sections);
                            courseSectionAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            CustomToast.showError(requireActivity(), "Σφάλμα ΤΜΗΜΑΤΩΝ: " + e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                CustomToast.showError(requireActivity(), "Σφάλμα ΜΑΘΗΜΑΤΑ: " + e.getMessage());
            }
        });
    }
}