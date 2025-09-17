package gr.ihu.eparousiologio.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.adapter.CourseAdapter;
import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.repository.AttendanceDAO;
import gr.ihu.eparousiologio.repository.AttendanceRepository;
import gr.ihu.eparousiologio.repository.CourseSectionDAO;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.ExcelExporter;
import gr.ihu.eparousiologio.util.MediaStoreFileSaver;
import gr.ihu.eparousiologio.util.OnResultListener;
import gr.ihu.eparousiologio.view.MainActivity;

public class ChooseCourseSectionToExportExcelFragment extends Fragment {

    private final CourseSectionDAO courseSectionDAO = new CourseSectionDAO();
    View rootView;
    AttendanceRepository attendanceRepo = new AttendanceDAO();
    CourseAdapter courseAdapter;

    public ChooseCourseSectionToExportExcelFragment() {
        // Required empty public constructor
    }

    public static ChooseCourseSectionToExportExcelFragment newInstance() {
        ChooseCourseSectionToExportExcelFragment fragment = new ChooseCourseSectionToExportExcelFragment();
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

        rootView = inflater.inflate(R.layout.fragment_choose_course_section_to_export_excel, container, false);
        RecyclerView courseSectionToExportExcelRV = rootView.findViewById(R.id.courseSectionToExportExcelRV);

        List<Course> coursesList = new ArrayList<>();
        courseSectionToExportExcelRV.setLayoutManager(new LinearLayoutManager(requireContext()));

        courseAdapter = new CourseAdapter(coursesList, requireContext(), course -> new Thread(() -> {
            requireActivity().runOnUiThread(() -> {
                CustomToast.showInfo(
                        requireActivity(),
                        "Γίνεται δημιουργία του αρχείου παρακαλώ περιμένετε");
            });
            attendanceRepo.fetchCourseAttendanceOnce(
                    course.getCourseId(),
                    new OnResultListener<>() {
                        @Override
                        public void onSuccess(AttendanceRepository.AttendanceSnapshot snap) {
                            try {
                                MediaStoreFileSaver saver = new MediaStoreFileSaver(requireContext());
                                ExcelExporter.exportCourse(course, snap, saver);
                                requireActivity().runOnUiThread(() -> {
                                    CustomToast.showSuccess(
                                            requireActivity(),
                                            "Αποθηκεύτηκε στο Downloads/eParousiologio: " + saver.getLastFilePathOrName()
                                    );
                                    ((MainActivity) requireActivity()).resetToMainMenu();
                                });
                            } catch (Exception e) {
                                requireActivity().runOnUiThread(() ->
                                        CustomToast.showError(requireActivity(), "Σφάλμα εξαγωγής: " + e.getMessage())
                                );
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            requireActivity().runOnUiThread(() ->
                                    CustomToast.showError(requireActivity(), "Σφάλμα ανάγνωσης παρουσιών: " + e.getMessage())
                            );
                        }
                    }
            );
        }).start());

        courseSectionToExportExcelRV.setAdapter(courseAdapter);

        loadData();

        return rootView;
    }

    private void loadData() {
        courseSectionDAO.getAllCourses(new OnResultListener<>() {
            @Override
            public void onSuccess(List<Course> courses) {
                courseAdapter.setCourses(courses);
            }

            @Override
            public void onFailure(Exception e) {
                CustomToast.showError(requireActivity(), "Σφάλμα ΜΑΘΗΜΑΤΑ: " + e.getMessage());
            }
        });
    }

}