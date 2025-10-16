package gr.ihu.eparousiologio.repository;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.CourseNote;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.util.OnResultListener;

public class CourseResubstitutionLogNoteSectionDAO implements CourseSectionRepository, CourseNoteRepository {

    private static final String COLLECTION_NAME_COURSES = "courses";
    private static final String COLLECTION_NAME_LABS = "labs";
    private static final String COLLECTION_NAME_NOTES = "notes";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void getAllCourses(OnResultListener<List<Course>> listener) {
        db.collection(COLLECTION_NAME_COURSES).get().addOnSuccessListener(querySnapshot -> {
            List<Course> courses = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                Course c = doc.toObject(Course.class);
                if (c != null) {
                    c.setCourseId(doc.getId());
                    courses.add(c);
                }
            }
            listener.onSuccess(courses);
        }).addOnFailureListener(listener::onFailure);
    }

    @Override
    public void getCourseById(String id, OnResultListener<Course> listener) {
        db.collection(COLLECTION_NAME_COURSES).document(id).get().addOnSuccessListener(doc -> {
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
        }).addOnFailureListener(listener::onFailure);
    }

    @Override
    public void getAllSectionsByCourseId(String courseId, OnResultListener<List<Section>> listener) {
        db.collection(COLLECTION_NAME_COURSES).document(courseId).collection(COLLECTION_NAME_LABS).get().addOnSuccessListener(querySnapshot -> {
            List<Section> sections = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                Section section = doc.toObject(Section.class);
                if (section != null) {
                    section.setLabId(doc.getId());
                    sections.add(section);
                }
            }
            listener.onSuccess(sections);
        }).addOnFailureListener(listener::onFailure);
    }

    @Override
    public void addNoteOnCourse(String courseId, String note, OnResultListener<Void> listener) {
        DocumentReference newDocRef = db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_NOTES).document();

        newDocRef.set(new CourseNote(newDocRef.getId(), note, false))
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void addLogOnCourse(String courseId, String log, OnResultListener<Void> listener) {
        DocumentReference newDocRef = db.collection(COLLECTION_NAME_COURSES)
                .document(courseId).collection(COLLECTION_NAME_NOTES)
                .document();

        newDocRef.set(new CourseNote(newDocRef.getId(), log, true))
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }


    @Override
    public void addResubstitutionNoteOnCourse(String courseId, String aem, String labId, String labName, OnResultListener<Void> listener) {
        this.checkIfAlreadyExistingResubstitution(courseId, aem, labId, new OnResultListener<>() {
            @Override
            public void onSuccess(Void result) {
                String note = "Aναπλήρωσε στο " + labName;
                addResubstitutionNoteOnCourse(courseId, note, aem, labId)
                        .thenRun(() -> listener.onSuccess(null))
                        .exceptionally(throwable -> {
                            if (throwable instanceof Exception) {
                                listener.onFailure((Exception) throwable);
                            } else {
                                listener.onFailure(new Exception(throwable));
                            }
                            return null;
                        });
            }

            @Override
            public void onFailure(Exception exception) {
                listener.onFailure(exception);
            }
        });
    }

    private CompletableFuture<Void> addResubstitutionNoteOnCourse(String courseId, String note, String aem, String labId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();

        db.collection("courses").document(courseId).collection("labs").get().addOnSuccessListener(labsSnapshot -> {
            if (labsSnapshot.isEmpty()) {
                future.completeExceptionally(new Exception("Δεν υπάρχουν εργαστηριακά τμήματα."));
                return;
            }

            List<CompletableFuture<DocumentSnapshot>> checks = new ArrayList<>();
            for (DocumentSnapshot labDoc : labsSnapshot.getDocuments()) {
                CompletableFuture<DocumentSnapshot> f = new CompletableFuture<>();
                db.collection("courses")
                        .document(courseId)
                        .collection("labs")
                        .document(labDoc.getId())
                        .collection("students")
                        .document(aem)
                        .get()
                        .addOnSuccessListener(f::complete)
                        .addOnFailureListener(f::completeExceptionally);
                checks.add(f);
            }

            CompletableFuture.allOf(checks.toArray(new CompletableFuture[0])).thenAccept(v -> {
                String foundLabId = checks.stream()
                        .map(CompletableFuture::join)
                        .filter(DocumentSnapshot::exists)
                        .map(doc -> Objects.requireNonNull(doc.getReference().getParent().getParent()).getId()).findFirst().orElse(null);

                if (foundLabId == null) {
                    future.completeExceptionally(new Exception("Ο " + aem + " δεν ανήκει σε κανένα εργαστήριο."));
                    return;
                }

                if (foundLabId.equals(labId)) {
                    future.completeExceptionally(new Exception("Ο " + aem + " ανήκει ήδη στο τρέχον τμήμα. Καταχωρήστε παρουσία από τη λίστα παρουσιών."));
                    return;
                }

                DocumentReference newDocRef = db.collection("courses")
                        .document(courseId)
                        .collection(COLLECTION_NAME_NOTES)
                        .document();

                CourseNote cn = new CourseNote(newDocRef.getId(), note, foundLabId, aem, false);

                newDocRef.set(cn)
                        .addOnSuccessListener(aVoid -> future.complete(null))
                        .addOnFailureListener(future::completeExceptionally);

            }).exceptionally(e -> {
                future.completeExceptionally(e);
                return null;
            });
        }).addOnFailureListener(future::completeExceptionally);

        return future;
    }

    @Override
    public void checkIfAlreadyExistingResubstitution(String courseId, String aem, String labId, OnResultListener<Void> listener) {
        db.collection(COLLECTION_NAME_COURSES)
                .document(courseId)
                .collection(COLLECTION_NAME_NOTES)
                .whereEqualTo("aem", aem)
                .whereEqualTo("labId", labId)
                .whereEqualTo("isLog", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        listener.onSuccess(null);
                    } else {
                        listener.onFailure(new Exception("Ο " + aem + " έχει ήδη δηλωθεί προς αναπλήρωση."));
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void getCourseNotesByCourseId(String courseId, OnResultListener<List<CourseNote>> listener) {
        db.collection(COLLECTION_NAME_COURSES)
                .document(courseId).collection(COLLECTION_NAME_NOTES)
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
                }).addOnFailureListener(listener::onFailure);
    }

}
