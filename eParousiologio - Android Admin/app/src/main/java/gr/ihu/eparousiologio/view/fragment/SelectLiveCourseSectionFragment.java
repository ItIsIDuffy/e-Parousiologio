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
import gr.ihu.eparousiologio.model.LiveCourse;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.repository.CourseSectionDAO;
import gr.ihu.eparousiologio.repository.LiveCourseRepositoryDAO;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.OnResultListener;
import gr.ihu.eparousiologio.view.MainActivity;

public class SelectLiveCourseSectionFragment extends Fragment {
    private final CourseSectionDAO courseSectionDAO = new CourseSectionDAO();
    View rootView;
    private ExpandableListView liveCourseSectionELV;
    private CourseSectionAdapter courseSectionAdapter;
    private List<Course> liveCourseSectionsList;
    private HashMap<Course, List<Section>> listDataChild;

    public SelectLiveCourseSectionFragment() {
    }

    public static SelectLiveCourseSectionFragment newInstance() {
        SelectLiveCourseSectionFragment fragment = new SelectLiveCourseSectionFragment();
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
        rootView = inflater.inflate(R.layout.fragment_select_live_course_section, container, false);

        liveCourseSectionELV = rootView.findViewById(R.id.liveCourseSectionELV);

        liveCourseSectionsList = new ArrayList<>();
        listDataChild = new HashMap<>();
        courseSectionAdapter = new CourseSectionAdapter(requireContext(), liveCourseSectionsList, listDataChild);
        liveCourseSectionELV.setAdapter(courseSectionAdapter);

        loadData();

        liveCourseSectionELV.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Course selectedCourse = (Course) courseSectionAdapter.getGroup(groupPosition);
            Section selectedSection = (Section) courseSectionAdapter.getChild(groupPosition, childPosition);


            if (selectedSection.isUnassigned()) {
                CustomToast.showInfo(requireActivity(), "Φοιτητές χωρίς ανάθεση δεν δηλώνουν παρουσίες");
                return true;
            }

            LiveCourse liveCourse = new LiveCourse(
                    selectedCourse.getCourseId(),
                    selectedCourse.getTitle(),
                    true,
                    selectedSection.getLabId(),
                    selectedSection.getName(),
                    "",
                    null
            );

            new LiveCourseRepositoryDAO().addOrReplaceLiveCourse(liveCourse, new OnResultListener<>() {
                @Override
                public void onSuccess(Void result) {
                    if (!isAdded()) return;
                    CustomToast.showSuccess(requireActivity(), "Σε εξέλιξη το εργαστήτριο: " + selectedCourse.getTitle() + " " + selectedSection.getName());
                    ((MainActivity) requireActivity()).resetToMainMenu();
                }

                @Override
                public void onFailure(Exception e) {
                    if (!isAdded()) return;
                    CustomToast.showError(requireActivity(), "Σφάλμα: " + e.getMessage());
                }
            });

            return true;
        });

        return rootView;
    }

    private void loadData() {
        courseSectionDAO.getAllCourses(new OnResultListener<>() {
            @Override
            public void onSuccess(List<Course> courses) {
                liveCourseSectionsList.clear();
                listDataChild.clear();

                for (Course course : courses) {
                    liveCourseSectionsList.add(course);

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