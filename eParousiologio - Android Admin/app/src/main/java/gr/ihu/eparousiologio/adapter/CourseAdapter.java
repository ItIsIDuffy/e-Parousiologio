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
import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.view.helper.CourseSingleTapListener;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    List<Course> courses;
    Context context;
    CourseSingleTapListener listener;

    public CourseAdapter(List<Course> courses, Context context, CourseSingleTapListener listener) {
        this.courses = courses;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.courseTitleMTV.setText(course.getTitle());
        holder.courseRootMCV.setOnClickListener(view -> listener.onSingleClick(course));
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCourses(List<Course> newCourses) {
        courses.clear();
        courses.addAll(newCourses);
        notifyDataSetChanged();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView courseRootMCV;
        MaterialTextView courseTitleMTV;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseRootMCV = itemView.findViewById(R.id.courseRootMCV);
            courseTitleMTV = itemView.findViewById(R.id.courseTitleMTV);
        }
    }
}
