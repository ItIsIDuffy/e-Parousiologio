package gr.ihu.eparousiologio.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class Section implements Parcelable {
    public static final Creator<Section> CREATOR = new Creator<Section>() {
        @Override
        public Section createFromParcel(Parcel in) {
            return new Section(in);
        }

        @Override
        public Section[] newArray(int size) {
            return new Section[size];
        }
    };
    private String labId;
    private String name;
    private int dayOfWeek;
    private String start;
    private String end;
    private List<Student> students = new ArrayList<>();
    private boolean unassigned;

    public Section() {
    }

    public Section(String labId, String name, int dayOfWeek, String start, String end, boolean unassigned) {
        this.labId = labId;
        this.name = name;
        this.dayOfWeek = dayOfWeek;
        this.start = start;
        this.end = end;
        this.unassigned = unassigned;
        this.students = new ArrayList<>();
    }

    // 🔹 Constructor για Parcelable
    protected Section(Parcel in) {
        labId = in.readString();
        name = in.readString();
        dayOfWeek = in.readInt();
        start = in.readString();
        end = in.readString();
        students = in.createTypedArrayList(Student.CREATOR);
        unassigned = in.readByte() != 0;
    }

    public boolean isUnassigned() {
        return unassigned;
    }

    public void setUnassigned(boolean v) {
        this.unassigned = v;
    }

    @PropertyName("labId")
    public String getLabId() {
        return labId;
    }

    @PropertyName("labId")
    public void setLabId(String labId) {
        this.labId = labId;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("dayOfWeek")
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    @PropertyName("dayOfWeek")
    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @PropertyName("start")
    public String getStart() {
        return start;
    }

    @PropertyName("start")
    public void setStart(String start) {
        this.start = start;
    }

    @PropertyName("end")
    public String getEnd() {
        return end;
    }

    @PropertyName("end")
    public void setEnd(String end) {
        this.end = end;
    }

    @PropertyName("students")
    public List<Student> getStudents() {
        return students;
    }

    @PropertyName("students")
    public void setStudents(List<Student> students) {
        this.students = (students != null) ? students : new ArrayList<>();
    }

    public void addStudent(Student student) {
        if (students == null) {
            students = new ArrayList<>();
        }
        this.students.add(student);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(labId);
        parcel.writeString(name);
        parcel.writeInt(dayOfWeek);
        parcel.writeString(start);
        parcel.writeString(end);
        parcel.writeTypedList(students);
        parcel.writeByte((byte) (unassigned ? 1 : 0));
    }
}
