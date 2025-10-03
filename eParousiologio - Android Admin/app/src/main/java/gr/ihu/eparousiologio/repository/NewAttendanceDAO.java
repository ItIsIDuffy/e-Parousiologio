package gr.ihu.eparousiologio.repository;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import gr.ihu.eparousiologio.model.NewAttendanceSnapshot;
import gr.ihu.eparousiologio.util.OnResultListener;

public class NewAttendanceDAO implements NewAttendanceRepository {

    private static final String COLLECTION_COURSES = "courses";
    private static final String COLLECTION_ATTENDANCE = "attendance";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void uploadDailyAttendanceRecord(NewAttendanceSnapshot snapshot, OnResultListener<Void> listener) {
        db.collection(COLLECTION_COURSES)
                .document(snapshot.getCourseId())
                .collection(COLLECTION_ATTENDANCE)
                .document(snapshot.getSnapshotId())
                .set(snapshot)
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void fetchAttendanceSnapshotBySnapshotId(String courseId, String snapshotId, OnResultListener<NewAttendanceSnapshot> listener) {
        db.collection(COLLECTION_COURSES)
                .document(courseId)
                .collection(COLLECTION_ATTENDANCE)
                .document(snapshotId)
                .get()
                .addOnSuccessListener(attendanceSnapshot -> listener.onSuccess(attendanceSnapshot.toObject(NewAttendanceSnapshot.class)))
                .addOnFailureListener(listener::onFailure);
    }


    @Override
    public void fetchAllCourseAttendanceSnapshots(String courseId, OnResultListener<List<NewAttendanceSnapshot>> listener) {
        db.collection(COLLECTION_COURSES)
                .document(courseId)
                .collection(COLLECTION_ATTENDANCE)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<NewAttendanceSnapshot> snapshotsList = querySnapshot.toObjects(NewAttendanceSnapshot.class);
                    listener.onSuccess(snapshotsList);
                })
                .addOnFailureListener(listener::onFailure);
    }
}
