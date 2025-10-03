package gr.ihu.eparousiologio.repository;

import java.util.List;

import gr.ihu.eparousiologio.model.CourseNote;
import gr.ihu.eparousiologio.util.OnResultListener;

public interface NoteCourseRepository {
    void addNoteOnCourse(String courseId, String note, OnResultListener<Void> listener);
    void getCourseNotesByCourseId(String courseId, OnResultListener<List<CourseNote>> listener);
}
