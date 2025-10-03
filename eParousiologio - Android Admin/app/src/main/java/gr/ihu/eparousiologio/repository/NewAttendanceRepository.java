package gr.ihu.eparousiologio.repository;

import java.util.List;

import gr.ihu.eparousiologio.model.NewAttendanceSnapshot;
import gr.ihu.eparousiologio.util.OnResultListener;

public interface NewAttendanceRepository {
    void uploadDailyAttendanceRecord(NewAttendanceSnapshot snapshot, OnResultListener<Void> listener);
    void fetchAttendanceSnapshotBySnapshotId(String courseId, String snapshotId, OnResultListener<NewAttendanceSnapshot> snapshot);
    void fetchAllCourseAttendanceSnapshots(String courseId, OnResultListener<List<NewAttendanceSnapshot>> snapshots);
}
