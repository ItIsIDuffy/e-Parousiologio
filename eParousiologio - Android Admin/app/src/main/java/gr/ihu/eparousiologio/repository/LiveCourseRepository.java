package gr.ihu.eparousiologio.repository;

import com.google.firebase.firestore.ListenerRegistration;

import gr.ihu.eparousiologio.model.LiveCourse;
import gr.ihu.eparousiologio.util.OnResultListener;

public interface LiveCourseRepository {
    ListenerRegistration getLiveCourse(OnResultListener<LiveCourse> listener);

    void addOrReplaceLiveCourse(LiveCourse liveCourse, OnResultListener<Void> listener);

    void updateLiveCourseAttendance(LiveCourse liveCourse, OnResultListener<Void> listener);

    void deleteLiveCourse(OnResultListener<Void> listener);
}
