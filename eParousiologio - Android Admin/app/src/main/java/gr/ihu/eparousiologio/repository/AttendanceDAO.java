package gr.ihu.eparousiologio.repository;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gr.ihu.eparousiologio.model.AttendanceEntry;
import gr.ihu.eparousiologio.util.OnResultListener;

public class AttendanceDAO implements AttendanceRepository {

    private static final String C_COURSES = "courses";
    private static final String C_ATTENDANCE = "attendance";
    private static final String C_ENTRIES = "entries";
    private static final Pattern SESSION_ID_LAB = Pattern.compile("^[^/]*?-(.+)$");
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void fetchCourseAttendanceOnce(String courseId, OnResultListener<AttendanceSnapshot> listener) {
        db.collection(C_COURSES)
                .document(courseId)
                .collection(C_ATTENDANCE)
                .get()
                .addOnSuccessListener(attendanceQuery -> {
                    if (attendanceQuery == null || attendanceQuery.isEmpty()) {
                        AttendanceSnapshot snap = new AttendanceSnapshot();
                        snap.sessionsByLab = new HashMap<>();
                        snap.entriesByLabAndAem = new HashMap<>();
                        listener.onSuccess(snap);
                        return;
                    }

                    List<Task<?>> entryTasks = new ArrayList<>();
                    Map<String, Integer> sessionsByLab = new HashMap<>();
                    Map<String, Map<String, List<AttendanceEntry>>> byLabThenAem = new HashMap<>();

                    for (DocumentSnapshot sessionDoc : attendanceQuery.getDocuments()) {
                        String labId = sessionDoc.getString("labId");
                        if (labId == null || labId.isEmpty()) {
                            String sid = sessionDoc.getId();
                            Matcher m = SESSION_ID_LAB.matcher(sid);
                            if (m.find()) labId = m.group(1);
                        }
                        if (labId == null || labId.isEmpty()) continue;

                        sessionsByLab.put(labId, sessionsByLab.getOrDefault(labId, 0) + 1);

                        String finalLabId = labId;
                        Task<?> t = sessionDoc.getReference()
                                .collection(C_ENTRIES)
                                .get()
                                .addOnSuccessListener(q -> {
                                    if (q == null || q.isEmpty()) return;
                                    for (QueryDocumentSnapshot eDoc : q) {
                                        AttendanceEntry e = eDoc.toObject(AttendanceEntry.class);
                                        if (e == null) continue;

                                        String aem = e.getStudentAEM();
                                        if (aem == null || aem.trim().isEmpty()) aem = eDoc.getId();

                                        String labKey = (e.getLabId() != null && !e.getLabId().isEmpty()) ? e.getLabId() : finalLabId;

                                        byLabThenAem
                                                .computeIfAbsent(labKey, k -> new HashMap<>())
                                                .computeIfAbsent(aem, k -> new ArrayList<>())
                                                .add(e);
                                    }
                                });
                        entryTasks.add(t);
                    }

                    Tasks.whenAllComplete(entryTasks)
                            .addOnSuccessListener(done -> {
                                AttendanceSnapshot snap = new AttendanceSnapshot();
                                snap.sessionsByLab = sessionsByLab;
                                snap.entriesByLabAndAem = byLabThenAem;
                                listener.onSuccess(snap);
                            })
                            .addOnFailureListener(listener::onFailure);
                })
                .addOnFailureListener(listener::onFailure);
    }
}
