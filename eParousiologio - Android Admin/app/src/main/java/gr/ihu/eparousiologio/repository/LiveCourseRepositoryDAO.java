package gr.ihu.eparousiologio.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import gr.ihu.eparousiologio.model.LiveCourse;
import gr.ihu.eparousiologio.util.OnResultListener;

public class LiveCourseRepositoryDAO implements LiveCourseRepository {

    private static final String COLLECTION_NAME_ROOT = "app_state";
    private static final String COLLECTION_NAME_LIVE = "live";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public ListenerRegistration getLiveCourse(OnResultListener<LiveCourse> listener) {
        return db.collection(COLLECTION_NAME_ROOT)
                .document(COLLECTION_NAME_LIVE)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        listener.onFailure(error);
                        return;
                    }

                    if (value != null && value.exists()) {
                        LiveCourse liveCourse = value.toObject(LiveCourse.class);
                        if (liveCourse != null) {
                            listener.onSuccess(liveCourse);
                        } else {
                            listener.onFailure(new Exception("Το LiveCourse είναι άδειο."));
                        }
                    } else {
                        listener.onFailure(new Exception("Δεν υπάρχει ενεργό μάθημα."));
                    }
                });
    }

    @Override
    public void addOrReplaceLiveCourse(LiveCourse liveCourse, OnResultListener<Void> listener) {
        db.collection(COLLECTION_NAME_ROOT)
                .document(COLLECTION_NAME_LIVE)
                .set(liveCourse)
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void updateLiveCourseAttendance(LiveCourse liveCourse, OnResultListener<Void> listener) {
        db.collection(COLLECTION_NAME_ROOT)
                .document(COLLECTION_NAME_LIVE)
                .set(liveCourse)
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void deleteLiveCourse(OnResultListener<Void> listener) {
        db.collection(COLLECTION_NAME_ROOT)
                .document(COLLECTION_NAME_LIVE)
                .delete()
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(listener::onFailure);
    }
}
