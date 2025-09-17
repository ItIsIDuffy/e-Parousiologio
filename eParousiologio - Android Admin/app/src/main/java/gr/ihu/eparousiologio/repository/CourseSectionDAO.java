package gr.ihu.eparousiologio.repository;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.util.OnResultListener;

public class CourseSectionDAO implements CourseSectionRepository {

    private static final String COLLECTION_NAME_COURSES = "courses";
    private static final String COLLECTION_NAME_LABS = "labs";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void getAllCourses(OnResultListener<List<Course>> listener) {
        db.collection(COLLECTION_NAME_COURSES)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Course> courses = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Course c = doc.toObject(Course.class);
                        if (c != null) {
                            c.setCourseId(doc.getId());
                            courses.add(c);
                        }
                    }
                    listener.onSuccess(courses);
                })
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void getCourseById(String id, OnResultListener<Course> listener) {
        db.collection(COLLECTION_NAME_COURSES)
                .document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Course course = doc.toObject(Course.class);
                        if (course == null) {
                            listener.onFailure(new Exception("Το μάθημα επιστρέφει null"));
                        } else {
                            course.setCourseId(doc.getId());
                            listener.onSuccess(course);
                        }
                    } else {
                        listener.onFailure(new Exception("Το μάθημα δεν βρέθηκε"));
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void getAllSectionsByCourseId(String courseId, OnResultListener<List<Section>> listener) {
        db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_LABS)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Section> sections = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Section section = doc.toObject(Section.class);
                        if (section != null) {
                            section.setLabId(doc.getId());
                            sections.add(section);
                        }
                    }
                    listener.onSuccess(sections);
                })
                .addOnFailureListener(listener::onFailure);
    }
}
