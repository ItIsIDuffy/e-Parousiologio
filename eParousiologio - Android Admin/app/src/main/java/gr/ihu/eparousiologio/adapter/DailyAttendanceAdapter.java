package gr.ihu.eparousiologio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.model.NewAttendanceEntry;

public class DailyAttendanceAdapter extends RecyclerView.Adapter<DailyAttendanceAdapter.StudentAttendanceEntryViewHolder> {

    private final List<NewAttendanceEntry> attendanceEntries;
    private final Context context;

    public DailyAttendanceAdapter(List<NewAttendanceEntry> attendanceEntries, Context context) {
        this.attendanceEntries = attendanceEntries;
        this.context = context;
    }

    public List<NewAttendanceEntry> getAttendanceEntries() {
        return attendanceEntries;
    }

    @NonNull
    @Override
    public DailyAttendanceAdapter.StudentAttendanceEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_student_entry, parent, false);
        return new DailyAttendanceAdapter.StudentAttendanceEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyAttendanceAdapter.StudentAttendanceEntryViewHolder holder, int position) {
        NewAttendanceEntry student = attendanceEntries.get(position);

        holder.attendanceStudentEntryAEMMTV.setText(student.getStudentAEM());
        holder.attendanceStudentEntryFullNameMTV.setText(student.getFullName());
        holder.attendanceStudentEntryOKCB.setOnCheckedChangeListener(null);
        holder.attendanceStudentEntryOKCB.setChecked(student.isWasPresent());
        holder.attendanceStudentEntryRootMCV.setOnClickListener(view ->
                holder.attendanceStudentEntryOKCB.setChecked(!holder.attendanceStudentEntryOKCB.isChecked())
        );

        holder.attendanceStudentEntryOKCB.setOnCheckedChangeListener(
                (CompoundButton buttonView, boolean isChecked) -> student.setWasPresent(isChecked)
        );
    }

    @Override
    public int getItemCount() {
        return attendanceEntries != null ? attendanceEntries.size() : 0;
    }

    public static class StudentAttendanceEntryViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView attendanceStudentEntryFullNameMTV, attendanceStudentEntryAEMMTV;
        MaterialCardView attendanceStudentEntryRootMCV;
        MaterialCheckBox attendanceStudentEntryOKCB;

        public StudentAttendanceEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            attendanceStudentEntryRootMCV = itemView.findViewById(R.id.attendanceStudentEntryRootMCV);
            attendanceStudentEntryAEMMTV = itemView.findViewById(R.id.attendanceStudentEntryAEMMTV);
            attendanceStudentEntryFullNameMTV = itemView.findViewById(R.id.attendanceStudentEntryFullNameMTV);
            attendanceStudentEntryOKCB = itemView.findViewById(R.id.attendanceStudentEntryOKCB);
        }
    }
}
