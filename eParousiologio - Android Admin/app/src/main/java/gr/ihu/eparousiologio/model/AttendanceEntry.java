package gr.ihu.eparousiologio.model;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class AttendanceEntry {

    private String studentAEM;
    private Timestamp at;
    private String courseId;
    private String courseTitle;
    private String labId;
    private String labName;
    private boolean ok;

    public AttendanceEntry() {
        // Απαραίτητο για Firestore serialization
    }

    @PropertyName("studentAEM")
    public String getStudentAEM() {
        return studentAEM;
    }

    @PropertyName("studentAEM")
    public void setStudentAEM(String studentAEM) {
        this.studentAEM = studentAEM;
    }

    @PropertyName("at")
    public Timestamp getAt() {
        return at;
    }

    @PropertyName("at")
    public void setAt(Timestamp at) {
        this.at = at;
    }

    @PropertyName("courseId")
    public String getCourseId() {
        return courseId;
    }

    @PropertyName("courseId")
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    @PropertyName("courseTitle")
    public String getCourseTitle() {
        return courseTitle;
    }

    @PropertyName("courseTitle")
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    @PropertyName("labId")
    public String getLabId() {
        return labId;
    }

    @PropertyName("labId")
    public void setLabId(String labId) {
        this.labId = labId;
    }

    @PropertyName("labName")
    public String getLabName() {
        return labName;
    }

    @PropertyName("labName")
    public void setLabName(String labName) {
        this.labName = labName;
    }

    @PropertyName("ok")
    public boolean isOk() {
        return ok;
    }

    @PropertyName("ok")
    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getDateString() {
        if (at == null) return "";
        return at.toDate().toInstant().toString().substring(0, 10);
    }

    @NonNull
    @Override
    public String toString() {
        return "AttendanceEntry{" +
                "aem=" + studentAEM +
                ", date=" + getDateString() +
                ", labId='" + labId + '\'' +
                ", labName='" + labName + '\'' +
                ", ok=" + ok +
                '}';
    }
}

