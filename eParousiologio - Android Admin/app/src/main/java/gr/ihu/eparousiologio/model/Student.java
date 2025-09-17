package gr.ihu.eparousiologio.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.PropertyName;

public class Student implements Parcelable {
    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
    private String fullName;
    private String studentAEM;
    private String semester;
    private boolean enrolled;

    public Student() {
    }

    public Student(String fullName, String studentAEM, String semester, boolean enrolled) {
        this.fullName = fullName;
        this.studentAEM = studentAEM;
        this.semester = semester;
        this.enrolled = enrolled;
    }

    protected Student(Parcel in) {
        fullName = in.readString();
        studentAEM = in.readString();
        semester = in.readString();
        enrolled = in.readByte() != 0;
    }

    @PropertyName("fullName")
    public String getFullName() {
        return fullName;
    }

    @PropertyName("fullName")
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @PropertyName("studentAEM")
    public String getStudentAEM() {
        return studentAEM;
    }

    @PropertyName("studentAEM")
    public void setStudentAEM(String studentAEM) {
        this.studentAEM = studentAEM;
    }

    @PropertyName("semester")
    public String getSemester() {
        return semester;
    }

    @PropertyName("semester")
    public void setSemester(String semester) {
        this.semester = semester;
    }

    @PropertyName("enrolled")
    public boolean isEnrolled() {
        return enrolled;
    }

    @PropertyName("enrolled")
    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(fullName);
        parcel.writeString(studentAEM);
        parcel.writeString(semester);
        parcel.writeByte((byte) (enrolled ? 1 : 0));
    }
}
