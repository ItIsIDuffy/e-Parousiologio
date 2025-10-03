package gr.ihu.eparousiologio.model;

import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewAttendanceSnapshot {
    private String snapshotId;
    private String courseId;
    private String courseTitle;
    private String labId;
    private String labName;
    @ServerTimestamp
    private Date savedAt;
    private List<NewAttendanceEntry> attendanceEntries;

    public NewAttendanceSnapshot() {}

    public NewAttendanceSnapshot(String snapshotId, String courseId, String courseTitle, String labId, String labName, List<NewAttendanceEntry> attendanceEntries) {
        this.snapshotId = snapshotId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.labId = labId;
        this.labName = labName;
        this.attendanceEntries = attendanceEntries;
    }

    public NewAttendanceSnapshot(String snapshotId, String courseId, String courseTitle, String labId, String labName, Date savedAt, List<NewAttendanceEntry> attendanceEntries) {
        this.snapshotId = snapshotId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.labId = labId;
        this.labName = labName;
        this.savedAt = savedAt;
        this.attendanceEntries = attendanceEntries;
    }

    @PropertyName("snapshotId")
    public String getSnapshotId() {
        return snapshotId;
    }
    @PropertyName("snapshotId")
    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
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

    @PropertyName("savedAt")
    public Date getSavedAt() {
        return savedAt;
    }
    @PropertyName("savedAt")
    public void setSavedAt(Date savedAt) {
        this.savedAt = savedAt;
    }

    @PropertyName("attendanceEntries")
    public List<NewAttendanceEntry> getAttendanceEntries() {
        return attendanceEntries;
    }
    @PropertyName("attendanceEntries")
    public void setAttendanceEntries(List<NewAttendanceEntry> attendanceEntries) {
        this.attendanceEntries = attendanceEntries;
    }

    public static String generateSnapshotId(String labId) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String datePart = formatter.format(new Date());
        return datePart + "-" + labId;
    }
}
