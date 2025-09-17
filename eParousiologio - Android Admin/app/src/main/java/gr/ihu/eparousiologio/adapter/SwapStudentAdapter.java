package gr.ihu.eparousiologio.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.view.helper.StudentSwapItemOnClickListener;

public class SwapStudentAdapter extends RecyclerView.Adapter<SwapStudentAdapter.StudentViewHolder> {

    private final List<Student> students;
    private final Context context;
    private final StudentSwapItemOnClickListener listener;

    public SwapStudentAdapter(List<Student> students, Context context, StudentSwapItemOnClickListener listener) {
        this.students = students;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SwapStudentAdapter.StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
        return new SwapStudentAdapter.StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SwapStudentAdapter.StudentViewHolder holder, int position) {
        Student student = students.get(position);

        holder.studentAEMMTV.setText(student.getStudentAEM());
        holder.studentFullNameMTV.setText(student.getFullName());
        holder.studentRootMCV.setOnClickListener(view -> listener.onItemClick(student));
    }

    @Override
    public int getItemCount() {
        return students != null ? students.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setStudents(List<Student> newStudents) {
        students.clear();
        students.addAll(newStudents);
        notifyDataSetChanged();
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