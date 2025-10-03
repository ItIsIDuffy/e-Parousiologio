package gr.ihu.eparousiologio.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.adapter.CourseAdapter;
import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.CourseNote;
import gr.ihu.eparousiologio.model.NewAttendanceSnapshot;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.repository.AttendanceDAO;
import gr.ihu.eparousiologio.repository.AttendanceRepository;
import gr.ihu.eparousiologio.repository.CourseSectionDAO;
import gr.ihu.eparousiologio.repository.CourseSectionRepository;
import gr.ihu.eparousiologio.repository.NewAttendanceDAO;
import gr.ihu.eparousiologio.repository.StudentRecordsRepositoryDAO;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.ExcelExporterNew;
import gr.ihu.eparousiologio.util.MediaStoreFileSaver;
import gr.ihu.eparousiologio.util.OnResultListener;
import gr.ihu.eparousiologio.view.MainActivity;

public class ChooseCourseSectionToExportExcelFragment extends Fragment {

    private final CourseSectionDAO courseSectionDAO = new CourseSectionDAO();
    View rootView;
    AttendanceRepository attendanceRepo = new AttendanceDAO();
    CourseAdapter courseAdapter;
    NewAttendanceDAO newAttendanceDAO = new NewAttendanceDAO();
    CourseSectionRepository courseRepo = new CourseSectionDAO();
    StudentRecordsRepositoryDAO studentRecordsRepositoryDAO = new StudentRecordsRepositoryDAO();

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
            requireActivity().runOnUiThread(() ->
                    CustomToast.showInfo(requireActivity(), "Γίνεται δημιουργία του αρχείου, παρακαλώ περιμένετε…")
            );

            // 1) Φέρε snapshots (νέα λογική)
            newAttendanceDAO.fetchAllCourseAttendanceSnapshots(course.getCourseId(), new OnResultListener<List<NewAttendanceSnapshot>>() {
                @Override public void onSuccess(List<NewAttendanceSnapshot> snapshots) {

                    // 2) Φέρε sections (για τίτλο εργαστηρίου & roster)
                    courseRepo.getAllSectionsByCourseId(course.getCourseId(), new OnResultListener<List<Section>>() {
                        @Override public void onSuccess(List<Section> sections) {
                            Map<String, Section> sectionsByLabId = new HashMap<>();
                            if (sections != null) {
                                for (Section s : sections) {
                                    if (s != null) sectionsByLabId.put(s.getLabId(), s);
                                }
                            }

                            // 3) Φέρε ΟΛΕΣ τις σημειώσεις του μαθήματος (course-wide)
                            courseSectionDAO.getCourseNotesByCourseId(course.getCourseId(), new OnResultListener<List<CourseNote>>() {
                                @Override public void onSuccess(List<CourseNote> courseNotes) {
                                    try {
                                        MediaStoreFileSaver saver = new MediaStoreFileSaver(requireContext());
                                        String fileName = "eParousiologio_" + course.getTitle() + ".xlsx";

                                        // 4) ΚΑΛΕΣΕ το exporter που δέχεται Output
                                        ExcelExporterNew.exportCourseNew(
                                                course,
                                                sectionsByLabId,
                                                snapshots,
                                                courseNotes,
                                                saver,          // <-- το Output
                                                fileName
                                        );

                                        requireActivity().runOnUiThread(() -> {
                                            CustomToast.showSuccess(
                                                    requireActivity(),
                                                    "Αποθηκεύτηκε στο Downloads/eParousiologio: " + saver.getLastFilePathOrName()
                                            );
                                            ((MainActivity) requireActivity()).resetToMainMenu();
                                        });

                                    } catch (Exception ex) {
                                        requireActivity().runOnUiThread(() ->
                                                CustomToast.showError(requireActivity(), "Σφάλμα εξαγωγής: " + ex.getMessage())
                                        );
                                    }
                                }

                                @Override public void onFailure(Exception e) {
                                    requireActivity().runOnUiThread(() ->
                                            CustomToast.showError(requireActivity(), "Σφάλμα σημειώσεων: " + e.getMessage())
                                    );
                                }
                            });
                        }

                        @Override public void onFailure(Exception e) {
                            requireActivity().runOnUiThread(() ->
                                    CustomToast.showError(requireActivity(), "Σφάλμα sections: " + e.getMessage())
                            );
                        }
                    });
                }

                @Override public void onFailure(Exception e) {
                    requireActivity().runOnUiThread(() ->
                            CustomToast.showError(requireActivity(), "Σφάλμα παρουσιών: " + e.getMessage())
                    );
                }
            });
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