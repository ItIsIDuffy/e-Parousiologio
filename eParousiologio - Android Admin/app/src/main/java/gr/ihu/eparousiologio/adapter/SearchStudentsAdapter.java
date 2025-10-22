package gr.ihu.eparousiologio.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.MessageFormat;
import java.util.List;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.model.LiveCourse;
import gr.ihu.eparousiologio.model.Student;

public class SearchStudentsAdapter extends RecyclerView.Adapter<SearchStudentsAdapter.StudentViewHolder> {
    private final LiveCourse liveCourse;
    private final OnStudentResultSelectedListener listener;
    private List<Student> students;

    public SearchStudentsAdapter(LiveCourse liveCourse, List<Student> students, OnStudentResultSelectedListener listener) {
        this.liveCourse = liveCourse;
        this.students = students;
        this.listener = listener;
    }

    public void updateList(List<Student> newStudents) {
        this.students = newStudents;
        notifyDataSetChanged();
    }

    public void addItems(List<Student> moreStudents) {
        int start = students.size();
        students.addAll(moreStudents);
        notifyItemRangeInserted(start, moreStudents.size());
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.studentFullNameMTV.setText(student.getFullName());
        holder.studentAEMMTV.setText(student.getStudentAEM());

        holder.studentRootMCV.setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_confirm_resubstitution, null);

            AlertDialog dialog = new AlertDialog.Builder(view.getContext()).setView(dialogView).create();

            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            MaterialTextView message = dialogView.findViewById(R.id.confirmResubstitutionMessageMTV);
            message.setText(MessageFormat.format("Πρόκειται να καταχωρήσετε τον φοιτητή {0} {1} προς αναπλήρωση στο {2} θέλετε να συνεχίσετε;", student.getFullName(), student.getStudentAEM(), liveCourse.getLabName()));

            dialogView.findViewById(R.id.confirmResubstitutionAcceptMB).setOnClickListener(v -> {
                listener.onStudentSelected(student);
                dialog.dismiss();
            });

            dialogView.findViewById(R.id.confirmResubstitutionCancelMB).setOnClickListener(v -> dialog.dismiss());

            dialog.show();

            if (dialog.getWindow() != null)
                dialog.getWindow().setLayout((int) (view.getContext().getResources().getDisplayMetrics().widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        });

    }

    @Override
    public int getItemCount() {
        return students != null ? students.size() : 0;
    }

    public interface OnStudentResultSelectedListener {
        void onStudentSelected(Student student);
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView studentAEMMTV, studentFullNameMTV;
        MaterialCardView studentRootMCV;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentAEMMTV = itemView.findViewById(R.id.studentAEMMTV);
            studentFullNameMTV = itemView.findViewById(R.id.studentFullNameMTV);
            studentRootMCV = itemView.findViewById(R.id.studentRootMCV);
        }
    }
}

