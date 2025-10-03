package gr.ihu.eparousiologio.model;

import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class CourseNote {
    private String noteId;
    private String message;

    @ServerTimestamp
    private Date createdAt;

    public CourseNote() {}

    public CourseNote(String noteId, String message) {
        this.noteId = noteId;
        this.message = message;
    }

    public CourseNote(String noteId, String message, Date createdAt) {
        this.noteId = noteId;
        this.message = message;
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

    @PropertyName("createdAt")
    public Date getCreatedAt() {
        return createdAt;
    }

    @PropertyName("createdAt")
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
