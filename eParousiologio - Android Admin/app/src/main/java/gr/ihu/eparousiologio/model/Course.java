package gr.ihu.eparousiologio.model;

import static gr.ihu.eparousiologio.model.Ids.sanitizeCourseId;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class Course implements Parcelable {
    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
    private String courseId;
    private String title;
    private int totalStudents;
    private String scheduleTz = "Europe/Athens";
    private List<Section> sections = new ArrayList<>();

    public Course() {
        this.sections = new ArrayList<>();
        this.scheduleTz = "Europe/Athens";
    }

    public Course(String title) {
        this();
        this.title = title;
        this.courseId = sanitizeCourseId(title);
    }

    public Course(String courseId, String title, int totalStudents, String scheduleTz) {
        this();
        this.courseId = courseId;
        this.title = title;
        this.totalStudents = totalStudents;
        this.scheduleTz = (scheduleTz != null) ? scheduleTz : "Europe/Athens";
    }

    protected Course(Parcel in) {
        courseId = in.readString();
        title = in.readString();
        totalStudents = in.readInt();
        scheduleTz = in.readString();
    }

    @PropertyName("courseId")
    public String getCourseId() {
        return courseId;
    }

    @PropertyName("courseId")
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    @PropertyName("title")
    public String getTitle() {
        return title;
    }

    @PropertyName("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @PropertyName("totalStudents")
    public int getTotalStudents() {
        return totalStudents;
    }

    @PropertyName("totalStudents")
    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    @PropertyName("scheduleTz")
    public String getScheduleTz() {
        return scheduleTz;
    }

    @PropertyName("scheduleTz")
    public void setScheduleTz(String scheduleTz) {
        this.scheduleTz = scheduleTz;
    }

    @PropertyName("sections")
    public List<Section> getSections() {
        return sections;
    }

    @PropertyName("sections")
    public void setSections(List<Section> sections) {
        this.sections = (sections != null) ? sections : new ArrayList<>();
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void updateTotalStudents() {
        int count = 0;
        for (Section section : sections) {
            if (section.getStudents() != null) {
                count += section.getStudents().size();
            }
        }
        this.totalStudents = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(courseId);
        parcel.writeString(title);
        parcel.writeInt(totalStudents);
        parcel.writeString(scheduleTz);
    }
}
