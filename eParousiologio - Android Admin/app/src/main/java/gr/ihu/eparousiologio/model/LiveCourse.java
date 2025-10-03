package gr.ihu.eparousiologio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class LiveCourse implements Parcelable {
    private String courseId;
    private String courseTitle;
    private boolean isOpen;
    private String labId;
    private String labName;
    private String note;

    @ServerTimestamp
    private Date openedAt;

    public LiveCourse() {}

    public LiveCourse(String courseId, String courseTitle, boolean isOpen,
                      String labId, String labName, String note, Date openedAt) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.isOpen = isOpen;
        this.labId = labId;
        this.labName = labName;
        this.note = note;
        this.openedAt = openedAt;
    }

    protected LiveCourse(Parcel in) {
        courseId = in.readString();
        courseTitle = in.readString();
        isOpen = in.readByte() != 0;
        labId = in.readString();
        labName = in.readString();
        note = in.readString();
        long tmpDate = in.readLong();
        openedAt = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Creator<LiveCourse> CREATOR = new Creator<LiveCourse>() {
        @Override
        public LiveCourse createFromParcel(Parcel in) {
            return new LiveCourse(in);
        }

        @Override
        public LiveCourse[] newArray(int size) {
            return new LiveCourse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(courseId);
        dest.writeString(courseTitle);
        dest.writeByte((byte) (isOpen ? 1 : 0));
        dest.writeString(labId);
        dest.writeString(labName);
        dest.writeString(note);
        dest.writeLong(openedAt != null ? openedAt.getTime() : -1);
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

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
