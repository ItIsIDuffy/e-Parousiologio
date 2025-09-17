package gr.ihu.eparousiologio.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import gr.ihu.eparousiologio.view.helper.AccessStudentRecordActionsListener;

public class AccessStudentRecordAdapter extends RecyclerView.Adapter<AccessStudentRecordAdapter.StudentViewHolder> {

    private final List<Student> students;
    private final Context context;
    private final AccessStudentRecordActionsListener listener;
    private final boolean isSectionUnassigned;

    public AccessStudentRecordAdapter(List<Student> students, Context context,
                                      AccessStudentRecordActionsListener listener) {
        this.students = students;
        this.context = context;
        this.listener = listener;
        this.isSectionUnassigned = false;
    }

    public AccessStudentRecordAdapter(boolean isSectionUnassigned, List<Student> students, Context context,
                                      AccessStudentRecordActionsListener listener) {
        this.students = students;
        this.context = context;
        this.listener = listener;
        this.isSectionUnassigned = isSectionUnassigned;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);

        holder.studentAEMMTV.setText(student.getStudentAEM());
        holder.studentFullNameMTV.setText(student.getFullName());

        if (!isSectionUnassigned) {
            holder.studentRootMCV.setOnClickListener(view -> {
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_student_options, null);

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(dialogView)
                        .create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                dialogView.findViewById(R.id.optionTransferStudentMTV).setOnClickListener(v -> {
                    listener.onTransfer(student);
                    dialog.dismiss();
                });

                dialogView.findViewById(R.id.optionSwapStudentMTV).setOnClickListener(v -> {
                    listener.onSwap(student);
                    dialog.dismiss();
                });

                dialogView.findViewById(R.id.optionDeleteStudentMTV).setOnClickListener(v -> {
                    listener.onDelete(student);
                    dialog.dismiss();
                });

                dialog.show();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setLayout(
                            (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8),
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                }
            });
        }
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

