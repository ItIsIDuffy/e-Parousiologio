package gr.ihu.eparousiologio.repository;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.util.OnResultListener;

public class StudentRecordsRepositoryDAO implements StudentRecordsRepository {

    private static final String COLLECTION_NAME_COURSES = "courses";
    private static final String COLLECTION_NAME_LABS = "labs";
    private static final String COLLECTION_NAME_STUDENTS = "students";
    private static final String COLLECTION_NAME_UNASSIGNED = "unassigned";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void addStudentToCourseSection(String courseId, String sectionId, Student student, OnResultListener<Void> listener) {
        DocumentReference targetDoc = db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_LABS)
                .document(sectionId)
                .collection(COLLECTION_NAME_STUDENTS)
                .document(student.getStudentAEM());

        DocumentReference unassignedDoc = db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_LABS)
                .document(COLLECTION_NAME_UNASSIGNED)
                .collection(COLLECTION_NAME_STUDENTS)
                .document(student.getStudentAEM());

        db.runTransaction(transaction -> {
                    DocumentSnapshot unassignedSnap = transaction.get(unassignedDoc);

                    transaction.set(targetDoc, student);

                    if (unassignedSnap.exists()) {
                        transaction.delete(unassignedDoc);
                    }

                    return null;
                })
                .addOnSuccessListener(unused -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }


    @Override
    public void transferStudent(String courseId, String sectionIdFrom, String sectionIdTo, Student student, OnResultListener<Void> listener) {
        DocumentReference sourceDoc = db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_LABS)
                .document(sectionIdFrom)
                .collection(COLLECTION_NAME_STUDENTS)
                .document(student.getStudentAEM());

        DocumentReference targetDoc = db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_LABS)
                .document(sectionIdTo)
                .collection(COLLECTION_NAME_STUDENTS)
                .document(student.getStudentAEM());

        db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(sourceDoc);

                    if (!snapshot.exists()) {
                        throw new FirebaseFirestoreException(
                                "Δεν βρέθηκε/εντοπίστηκε εγγεγραμένος φοιτητής στα εργαστήρια",
                                FirebaseFirestoreException.Code.NOT_FOUND
                        );
                    }

                    transaction.set(targetDoc, student);
                    transaction.delete(sourceDoc);

                    return null;
                })
                .addOnSuccessListener(unused -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void swapStudents(String courseId, String sectionId1, Student student1, String sectionId2, Student student2, OnResultListener<Void> listener) {

        DocumentReference oldDocRef1 = db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_LABS)
                .document(sectionId1)
                .collection(COLLECTION_NAME_STUDENTS)
                .document(student1.getStudentAEM());

        DocumentReference oldDocRef2 = db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_LABS)
                .document(sectionId2)
                .collection(COLLECTION_NAME_STUDENTS)
                .document(student2.getStudentAEM());

        DocumentReference newDocRef1 = db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_LABS)
                .document(sectionId1)
                .collection(COLLECTION_NAME_STUDENTS)
                .document(student2.getStudentAEM());

        DocumentReference newDocRef2 = db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_LABS)
                .document(sectionId2)
                .collection(COLLECTION_NAME_STUDENTS)
                .document(student1.getStudentAEM());

        db.runTransaction(transaction -> {
                    transaction.delete(oldDocRef1);
                    transaction.delete(oldDocRef2);

                    transaction.set(newDocRef1, student2);
                    transaction.set(newDocRef2, student1);
                    return null;
                })
                .addOnSuccessListener(unused -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void deleteStudentFromSection(String courseId, String sectionId, Student student, OnResultListener<Void> listener) {
        db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_LABS)
                .document(sectionId)
                .collection(COLLECTION_NAME_STUDENTS)
                .document(student.getStudentAEM())
                .delete()
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public ListenerRegistration listenToStudentsInSection(String courseId, String sectionId, OnResultListener<List<Student>> listener) {
        return db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_LABS)
                .document(sectionId)
                .collection(COLLECTION_NAME_STUDENTS)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        listener.onFailure(e);
                        return;
                    }
                    if (snapshots == null) {
                        listener.onFailure(new Exception("No data"));
                        return;
                    }

                    List<Student> students = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Student s = doc.toObject(Student.class);
                        if (s != null) students.add(s);
                    }
                    listener.onSuccess(students);
                });
    }
}