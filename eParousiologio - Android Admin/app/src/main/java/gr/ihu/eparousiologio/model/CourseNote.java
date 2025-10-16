package gr.ihu.eparousiologio.model;

import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CourseNote {
    private String noteId;
    private String message;
    private String labId;
    private String aem;
    private Boolean isLog;
    @ServerTimestamp
    private Date createdAt;

    public CourseNote() {
    }

    public CourseNote(String noteId, String message, boolean isLog) {
        this.noteId = noteId;
        this.message = message;
        this.isLog = isLog;
    }

    public CourseNote(String noteId, String message, String labId, String aem, boolean isLog) {
        this.noteId = noteId;
        this.message = message;
        this.labId = labId;
        this.aem = aem;
        this.isLog = isLog;
    }

    public CourseNote(String noteId, String message, String labId, String aem, boolean isLog, Date createdAt) {
        this.noteId = noteId;
        this.message = message;
        this.labId = labId;
        this.aem = aem;
        this.isLog = isLog;
        this.createdAt = createdAt;
    }

    @PropertyName("noteId")
    public String getNoteId() {
        return noteId;
    }

    @PropertyName("noteId")
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    @PropertyName("message")
    public String getMessage() {
        return message;
    }

    @PropertyName("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @PropertyName("labId")
    public String getLabId() {
        return labId;
    }

    @PropertyName("labId")
    public void setLabId(String labId) {
        this.labId = labId;
    }

    @PropertyName("aem")
    public String getAem() {
        return aem;
    }

    @PropertyName("aem")
    public void setAem(String aem) {
        this.aem = aem;
    }

    @PropertyName("isLog")
    public boolean isLog() {
        return isLog;
    }

    @PropertyName("isLog")
    public void setLog(boolean log) {
        isLog = log;
    }

    @PropertyName("createdAt")
    public Date getCreatedAt() {
        return createdAt;
    }

    @PropertyName("createdAt")
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
