package gr.ihu.eparousiologio.repository;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.util.OnResultListener;

public class FirebaseBatchUploader {
    private final FirebaseFirestore db;

    public FirebaseBatchUploader() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void uploadCourse(Course course, OnResultListener<Void> listener) {
        WriteBatch batch = db.batch();

        String courseId = course.getCourseId();
        if (courseId == null || courseId.trim().isEmpty()) {
            if (listener != null)
                listener.onFailure(new IllegalArgumentException("Missing courseId"));
            return;
        }

        DocumentReference courseRef = db.collection("courses").document(courseId);

        Map<String, Object> courseData = new HashMap<>();
        courseData.put("courseId", courseId);
        courseData.put("title", course.getTitle());
        courseData.put("totalStudents", course.getTotalStudents());
        courseData.put("timestamp", FieldValue.serverTimestamp());

        batch.set(courseRef, courseData);

        for (Section section : course.getSections()) {
            String labId = section.getLabId();
            if (labId == null || labId.trim().isEmpty()) {
                continue; // skip
            }
            DocumentReference labRef = courseRef.collection("labs").document(labId);

            Map<String, Object> labData = new HashMap<>();
            labData.put("labId", labId);
            labData.put("name", section.getName());
            labData.put("dayOfWeek", section.getDayOfWeek());
            labData.put("start", section.getStart());
            labData.put("end", section.getEnd());
            labData.put("studentCount", section.getStudents().size());
            labData.put("unassigned", section.isUnassigned());

            batch.set(labRef, labData);

            for (Student student : section.getStudents()) {
                DocumentReference studentRef = labRef.collection("students")
                        .document(student.getStudentAEM());

                Map<String, Object> s = new HashMap<>();
                s.put("fullName", student.getFullName());
                s.put("studentAEM", student.getStudentAEM());
                s.put("semester", student.getSemester());
                s.put("enrolled", student.isEnrolled());

                batch.set(studentRef, s);
            }
        }

        batch.commit()
                .addOnSuccessListener(unused -> {
                    Log.d("FIREBASE_BATCH", "OK upload: " + course.getTitle() + " (" + courseId + ")");
                    if (listener != null) listener.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE_BATCH", "Σφάλμα ανεβάσματος: " + e.getMessage(), e);
                    if (listener != null) listener.onFailure(e);
                });
    }

}
