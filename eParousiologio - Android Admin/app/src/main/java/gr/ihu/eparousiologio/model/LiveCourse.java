package gr.ihu.eparousiologio.model;

import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class LiveCourse {
    private String courseId;
    private String courseTitle;
    private boolean isOpen;
    private String labId;
    private String labName;
    private String note;

    @ServerTimestamp
    private Date openedAt;

    public LiveCourse() {
    }

    public LiveCourse(String courseId, String courseTitle, boolean isOpen, String labId, String labName, String note, Date openedAt) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.isOpen = isOpen;
        this.labId = labId;
        this.labName = labName;
        this.note = note;
        this.openedAt = openedAt;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    // getters
    @PropertyName("courseId")
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    @PropertyName("courseTitle")
    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    @PropertyName("isOpen")
    public boolean getIsOpen() {
        return isOpen;
    }

    @PropertyName("labId")
    public String getLabId() {
        return labId;
    }

    public void setLabId(String labId) {
        this.labId = labId;
    }

    @PropertyName("labName")
    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    @PropertyName("note")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @PropertyName("openedAt")
    public Date getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Date openedAt) {
        this.openedAt = openedAt;
    }
}
