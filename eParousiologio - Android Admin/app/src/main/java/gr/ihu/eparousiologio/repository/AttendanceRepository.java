package gr.ihu.eparousiologio.repository;

import java.util.List;

import gr.ihu.eparousiologio.model.AttendanceSnapshot;
import gr.ihu.eparousiologio.util.OnResultListener;

public interface AttendanceRepository {
    void uploadDailyAttendanceRecord(AttendanceSnapshot snapshot, OnResultListener<Void> listener);
    void fetchAttendanceSnapshotBySnapshotId(String courseId, String snapshotId, OnResultListener<AttendanceSnapshot> snapshot);
    void fetchAllCourseAttendanceSnapshots(String courseId, OnResultListener<List<AttendanceSnapshot>> snapshots);
}
