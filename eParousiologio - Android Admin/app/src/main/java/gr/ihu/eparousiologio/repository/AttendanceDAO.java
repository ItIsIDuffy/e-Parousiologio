package gr.ihu.eparousiologio.repository;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import gr.ihu.eparousiologio.model.AttendanceSnapshot;
import gr.ihu.eparousiologio.util.OnResultListener;

public class AttendanceDAO implements AttendanceRepository {

    private static final String COLLECTION_COURSES = "courses";
    private static final String COLLECTION_ATTENDANCE = "attendance";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void uploadDailyAttendanceRecord(AttendanceSnapshot snapshot, OnResultListener<Void> listener) {
        db.collection(COLLECTION_COURSES)
                .document(snapshot.getCourseId())
                .collection(COLLECTION_ATTENDANCE)
                .document(snapshot.getSnapshotId())
                .set(snapshot)
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void fetchAttendanceSnapshotBySnapshotId(String courseId, String snapshotId, OnResultListener<AttendanceSnapshot> listener) {
        db.collection(COLLECTION_COURSES)
                .document(courseId)
                .collection(COLLECTION_ATTENDANCE)
                .document(snapshotId)
                .get()
                .addOnSuccessListener(attendanceSnapshot -> listener.onSuccess(attendanceSnapshot.toObject(AttendanceSnapshot.class)))
                .addOnFailureListener(listener::onFailure);
    }


    @Override
    public void fetchAllCourseAttendanceSnapshots(String courseId, OnResultListener<List<AttendanceSnapshot>> listener) {
        db.collection(COLLECTION_COURSES)
                .document(courseId)
                .collection(COLLECTION_ATTENDANCE)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<AttendanceSnapshot> snapshotsList = querySnapshot.toObjects(AttendanceSnapshot.class);
                    listener.onSuccess(snapshotsList);
                })
                .addOnFailureListener(listener::onFailure);
    }
}
