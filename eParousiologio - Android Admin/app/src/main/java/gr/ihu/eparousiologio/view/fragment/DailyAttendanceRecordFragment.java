package gr.ihu.eparousiologio.view.fragment;

import android.annotation.SuppressLint;
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
import gr.ihu.eparousiologio.adapter.DailyAttendanceAdapter;
import gr.ihu.eparousiologio.model.LiveCourse;
import gr.ihu.eparousiologio.model.NewAttendanceEntry;
import gr.ihu.eparousiologio.model.NewAttendanceSnapshot;
import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.repository.NewAttendanceDAO;
import gr.ihu.eparousiologio.repository.StudentRecordsRepositoryDAO;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.OnResultListener;
import gr.ihu.eparousiologio.view.MainActivity;

public class DailyAttendanceRecordFragment extends Fragment {
    private static final String ARG_FRAGMENT_LIVE_COURSE = "liveCourse";
    private final StudentRecordsRepositoryDAO studentRecordsRepositoryDAO = new StudentRecordsRepositoryDAO();
    private final NewAttendanceDAO newAttendanceDAO = new NewAttendanceDAO();
    private LiveCourse liveCourse;
    private View rootView;
    private RecyclerView dailyAttendanceRecordRV;
    private MaterialButton dailyAttendanceRecordSubmitMB;
    private DailyAttendanceAdapter dailyAttendanceAdapter;
    private ListenerRegistration studentsRegistrationListener;

    public DailyAttendanceRecordFragment() {
        // Required empty public constructor
    }

    public static DailyAttendanceRecordFragment newInstance(LiveCourse liveCourse) {
        DailyAttendanceRecordFragment fragment = new DailyAttendanceRecordFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FRAGMENT_LIVE_COURSE, liveCourse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            liveCourse = getArguments().getParcelable(ARG_FRAGMENT_LIVE_COURSE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_daily_attendance_record, container, false);
        initializeUI();

        String snapshotId = NewAttendanceSnapshot.generateSnapshotId(liveCourse.getLabId());

        newAttendanceDAO.fetchAttendanceSnapshotBySnapshotId(
                liveCourse.getCourseId(), snapshotId,
                new OnResultListener<>() {
                    @Override
                    public void onSuccess(NewAttendanceSnapshot snapshot) {
                        List<NewAttendanceEntry> attendanceEntries = new ArrayList<>();

                        if (snapshot != null && snapshot.getAttendanceEntries() != null) {
                            attendanceEntries.addAll(snapshot.getAttendanceEntries());
                        }

                        dailyAttendanceAdapter = new DailyAttendanceAdapter(attendanceEntries, requireContext());
                        dailyAttendanceRecordRV.setAdapter(dailyAttendanceAdapter);

                        studentsRegistrationListener = studentRecordsRepositoryDAO.listenToStudentsInSection(
                                liveCourse.getCourseId(), liveCourse.getLabId(),
                                new OnResultListener<>() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onSuccess(List<Student> students) {
                                        for (Student s : students) {
                                            boolean alreadyExists = false;
                                            for (NewAttendanceEntry entry : attendanceEntries) {
                                                if (entry.getStudentAEM().equals(s.getStudentAEM())) {
                                                    alreadyExists = true;
                                                    break;
                                                }
                                            }
                                            if (!alreadyExists) {
                                                attendanceEntries.add(new NewAttendanceEntry(
                                                        s.getStudentAEM(),
                                                        s.getFullName(),
                                                        liveCourse.getCourseId(),
                                                        liveCourse.getCourseTitle(),
                                                        liveCourse.getLabId(),
                                                        liveCourse.getLabName(),
                                                        false
                                                ));
                                            }
                                        }
                                        dailyAttendanceAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        CustomToast.showError(requireActivity(), "Αδυναμία live ενημέρωσης φοιτητών.");
                                    }
                                }
                        );

                        dailyAttendanceRecordSubmitMB.setOnClickListener(view ->
                                newAttendanceDAO.uploadDailyAttendanceRecord(
                                        new NewAttendanceSnapshot(
                                                snapshotId,
                                                liveCourse.getCourseId(),
                                                liveCourse.getCourseTitle(),
                                                liveCourse.getLabId(),
                                                liveCourse.getLabName(),
                                                dailyAttendanceAdapter.getAttendanceEntries()
                                        ),
                                        new OnResultListener<Void>() {
                                            @Override
                                            public void onSuccess(Void result) {
                                                CustomToast.showSuccess(requireActivity(),
                                                        "Επιτυχία ενημέρωσης παρουσιών για το " + liveCourse.getLabName());
                                                ((MainActivity) requireActivity()).resetToMainMenu();
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                CustomToast.showError(requireActivity(),
                                                        "Αδυναμία καταχώρησης παρουσιών για το " + liveCourse.getLabName());
                                            }
                                        }
                                )
                        );
                    }

                    @Override
                    public void onFailure(Exception e) {
                        studentsRegistrationListener = studentRecordsRepositoryDAO.listenToStudentsInSection(
                                liveCourse.getCourseId(), liveCourse.getLabId(),
                                new OnResultListener<List<Student>>() {
                                    @Override
                                    public void onSuccess(List<Student> students) {
                                        List<NewAttendanceEntry> attendanceEntries = new ArrayList<>();
                                        for (Student s : students) {
                                            attendanceEntries.add(new NewAttendanceEntry(
                                                    s.getStudentAEM(),
                                                    s.getFullName(),
                                                    liveCourse.getCourseId(),
                                                    liveCourse.getCourseTitle(),
                                                    liveCourse.getLabId(),
                                                    liveCourse.getLabName(),
                                                    false
                                            ));
                                        }
                                        dailyAttendanceAdapter = new DailyAttendanceAdapter(attendanceEntries, requireContext());
                                        dailyAttendanceRecordRV.setAdapter(dailyAttendanceAdapter);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        CustomToast.showError(requireActivity(), "Αδυναμία φόρτωσης φοιτητών.");
                                    }
                                }
                        );
                    }
                }
        );
        return rootView;
    }

    private void initializeUI() {
        MaterialTextView dailyAttendanceRecordTitleMTV = rootView.findViewById(R.id.dailyAttendanceRecordTitleMTV);
        dailyAttendanceRecordRV = rootView.findViewById(R.id.dailyAttendanceRecordRV);
        dailyAttendanceRecordSubmitMB = rootView.findViewById(R.id.dailyAttendanceRecordSubmitMB);
        dailyAttendanceRecordRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        dailyAttendanceRecordTitleMTV.setText(MessageFormat.format("Μάθημα {0}\nΤμήμα {1}", liveCourse.getCourseTitle(), liveCourse.getLabName()));
    }

    @Override
    public void onStop() {
        if (studentsRegistrationListener != null) {
            studentsRegistrationListener.remove();
        }
        super.onStop();
    }
}