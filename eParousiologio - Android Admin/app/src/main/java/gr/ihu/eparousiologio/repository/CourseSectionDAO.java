package gr.ihu.eparousiologio.repository;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.CourseNote;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.util.OnResultListener;

public class CourseSectionDAO implements CourseSectionRepository, NoteCourseRepository {

    private static final String COLLECTION_NAME_COURSES = "courses";
    private static final String COLLECTION_NAME_LABS = "labs";
    private static final String COLLECTION_NAME_NOTES = "notes";
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

    @Override
    public void addNoteOnCourse(String courseId, String noteMessage, OnResultListener<Void> listener) {
        DocumentReference newDocRef = db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_NOTES)
                .document();

        newDocRef.set(new CourseNote(newDocRef.getId(), noteMessage))
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void getCourseNotesByCourseId(String courseId, OnResultListener<List<CourseNote>> listener) {
        db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_NOTES)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<CourseNote> notes = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        CourseNote cn = doc.toObject(CourseNote.class);
                        if (cn != null) {
                            notes.add(cn);
                        }
                    }
                    listener.onSuccess(notes);
                })
                .addOnFailureListener(listener::onFailure);
    }

}
