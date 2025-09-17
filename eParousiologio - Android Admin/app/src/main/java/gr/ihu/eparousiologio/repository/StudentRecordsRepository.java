package gr.ihu.eparousiologio.repository;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.util.OnResultListener;

public interface StudentRecordsRepository {

    void addStudentToCourseSection(String courseId, String sectionId, Student student, OnResultListener<Void> listener);

    void transferStudent(String courseId, String sectionIdFrom, String sectionIdTo, Student student, OnResultListener<Void> listener);

    void swapStudents(String courseId, String sectionId1, Student student1, String sectionId2, Student student2, OnResultListener<Void> listener);

    void deleteStudentFromSection(String courseId, String sectionId, Student student, OnResultListener<Void> listener);

    ListenerRegistration listenToStudentsInSection(String courseId, String sectionId, OnResultListener<List<Student>> listener);
}
