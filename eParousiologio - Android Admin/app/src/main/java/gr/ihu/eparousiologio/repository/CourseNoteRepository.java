package gr.ihu.eparousiologio.repository;

import java.util.List;

import gr.ihu.eparousiologio.model.CourseNote;
import gr.ihu.eparousiologio.util.OnResultListener;

public interface CourseNoteRepository {
    void addLogOnCourse(String courseId, String log, OnResultListener<Void> listener);
    void addNoteOnCourse(String courseId, String note, OnResultListener<Void> listener);
    void addResubstitutionNoteOnCourse(String courseId, String aem, String labId, String labName, OnResultListener<Void> listener);
    void getCourseNotesByCourseId(String courseId, OnResultListener<List<CourseNote>> listener);
}
