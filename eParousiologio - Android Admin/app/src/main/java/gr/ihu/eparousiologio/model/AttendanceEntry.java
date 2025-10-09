package gr.ihu.eparousiologio.model;

import com.google.firebase.firestore.PropertyName;
import java.util.Date;

public class AttendanceEntry {
    private String studentAEM;
    private String fullName;
    private String courseId;
    private String courseTitle;
    private String labId;
    private String labName;
    private boolean wasPresent;
    private Date presentAt;

    public AttendanceEntry() {}

    public AttendanceEntry(String studentAEM, String fullName, String courseId, String courseTitle, String labId, String labName, boolean wasPresent) {
        this.studentAEM = studentAEM;
        this.fullName = fullName;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.labId = labId;
        this.labName = labName;
        this.wasPresent = wasPresent;
    }

    @PropertyName("studentAEM")
    public String getStudentAEM() {
        return studentAEM;
    }
    @PropertyName("studentAEM")
    public void setStudentAEM(String studentAEM) {
        this.studentAEM = studentAEM;
    }
    @PropertyName("fullName")
    public String getFullName() {
        return fullName;
    }
    @PropertyName("fullName")
    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    @PropertyName("wasPresent")
    public boolean isWasPresent() {
        return wasPresent;
    }
    @PropertyName("wasPresent")
    public void setWasPresent(boolean wasPresent) {
        this.wasPresent = wasPresent;
    }

    @PropertyName("presentAt")
    public Date getPresentAt() {
        return presentAt;
    }
    @PropertyName("presentAt")
    public void setPresentAt(Date presentAt) {
        this.presentAt = presentAt;
    }
}