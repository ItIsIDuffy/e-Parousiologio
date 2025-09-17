package gr.ihu.eparousiologio.repository;

import java.util.List;

import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.util.OnResultListener;

public interface CourseSectionRepository {
    void getAllCourses(OnResultListener<List<Course>> listener);

    void getCourseById(String id, OnResultListener<Course> listener);

    void getAllSectionsByCourseId(String id, OnResultListener<List<Section>> listener);
}
