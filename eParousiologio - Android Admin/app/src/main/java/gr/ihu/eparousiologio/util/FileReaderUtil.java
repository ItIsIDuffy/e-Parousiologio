package gr.ihu.eparousiologio.util;

import static gr.ihu.eparousiologio.model.Ids.sanitizeCourseId;
import static gr.ihu.eparousiologio.model.Ids.sanitizeLabId;
import static gr.ihu.eparousiologio.util.CustomToast.showError;
import static gr.ihu.eparousiologio.util.CustomToast.showSuccess;
import static gr.ihu.eparousiologio.util.CustomToast.showWarning;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.repository.FirebaseBatchUploader;

public class FileReaderUtil {

    private static final Pattern COURSE_HEADER =
            Pattern.compile("^(?<title>.+?)\\s+Φοιτητές\\s*:\\s*(?<total>\\d+)$");
    private static final Pattern SECTION_HEADER =
            Pattern.compile("^Τμήμα\\s+(?:(?<code>[A-Za-zΑ-Ωα-ω0-9]+)\\s+)?(?<day>Δευτέρα|Τρίτη|Τετάρτη|Πέμπτη|Παρασκευή|Σάββατο|Κυριακή)\\s+(?<from>\\d{1,2})-(?<to>\\d{1,2})$");
    private static final Pattern STUDENT_LINE =
            Pattern.compile("^\\s*\\d+\\.\\s+(?<last>.+?)\\s{2,}(?<first>.+?)\\s+(?<aem>\\d{3,6})\\s+(?<sem>\\d+ο)\\s+(?<ok>NAI|OXI)$");
    private final Context context;

    public FileReaderUtil(Context context) {
        this.context = context;
    }

    private static int dayOfWeekToNumber(String grDay) {
        switch (grDay) {
            case "Δευτέρα":
                return 1;
            case "Τρίτη":
                return 2;
            case "Τετάρτη":
                return 3;
            case "Πέμπτη":
                return 4;
            case "Παρασκευή":
                return 5;
            case "Σάββατο":
                return 6;
            case "Κυριακή":
                return 7;
            default:
                return 0;
        }
    }

    public void readTextFromUri(Uri uri) {
        if (context == null) return;

        try (InputStream is = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, Charset.forName("windows-1253"))
             )) {

            Course course = parseElabs(reader);
            Activity activity = (Activity) context;

            if (course == null) {
                showWarning(activity, "Δεν βρέθηκαν δεδομένα μαθήματος.");
                return;
            }

            course.updateTotalStudents();

            FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
            if (u == null) {
                showError(activity, "Δεν είστε συνδεδεμένος ως καθηγητής.");
                return;
            }

            new FirebaseBatchUploader().uploadCourse(
                    course,
                    new OnResultListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            showSuccess(activity, "Τα δεδομένα αποθηκεύτηκαν επιτυχώς.");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            showError(activity, "Αποτυχία αποθήκευσης: " + e.getMessage());
                        }
                    }
            );

        } catch (Exception e) {
            showError((Activity) context, "Σφάλμα ανάγνωσης αρχείου: " + e.getMessage());
        }
    }

    private Course parseElabs(BufferedReader reader) throws Exception {
        Course course = null;
        Section currentSection = null;
        boolean inUnassigned = false;

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("----") || line.startsWith("—")) continue;
            if (line.startsWith("Ικανοποίηση της 1ης επιλογής")) break;

            Matcher mHeader = COURSE_HEADER.matcher(line);
            if (mHeader.find() && course == null) {
                course = new Course();
                course.setTitle(mHeader.group("title").trim());
                course.setCourseId(sanitizeCourseId(course.getTitle()));
                continue;
            }

            Matcher mSec = SECTION_HEADER.matcher(line);
            if (mSec.find()) {
                currentSection = new Section();
                String code = mSec.group("code");
                String day = mSec.group("day");
                String from = mSec.group("from");
                String to = mSec.group("to");

                currentSection.setName(code != null ? code : day + " " + from + "-" + to);
                currentSection.setDayOfWeek(dayOfWeekToNumber(day));
                currentSection.setStart(String.format("%02d:00", Integer.parseInt(from)));
                currentSection.setEnd(String.format("%02d:00", Integer.parseInt(to)));
                currentSection.setLabId(sanitizeLabId(day, from, code));

                if (course != null) course.addSection(currentSection);
                inUnassigned = false;
                continue;
            }

            if (line.startsWith("Φοιτητές χωρίς Ανάθεση")) {
                currentSection = new Section();
                currentSection.setName("Χωρίς Ανάθεση");
                currentSection.setLabId("unassigned");
                currentSection.setDayOfWeek(0);
                currentSection.setStart(null);
                currentSection.setEnd(null);
                currentSection.setUnassigned(true);
                if (course != null) course.addSection(currentSection);
                inUnassigned = true;
                continue;
            }

            Matcher mStud = STUDENT_LINE.matcher(line);
            if (mStud.find() && currentSection != null) {
                String fullName = mStud.group("last").trim() + " " + mStud.group("first").trim();
                String aem = mStud.group("aem").trim();
                String semester = mStud.group("sem").trim();
                boolean enrolled = !inUnassigned;

                currentSection.addStudent(new Student(fullName, aem, semester, enrolled));
            }
        }
        return course;
    }
}
