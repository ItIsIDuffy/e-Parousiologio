package gr.ihu.eparousiologio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.Section;

public class CourseSectionAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<Course> listDataHeader;
    private final HashMap<Course, List<Section>> listDataChild;

    public CourseSectionAdapter(Context context, List<Course> listDataHeader, HashMap<Course, List<Section>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Section> children = listDataChild.get(listDataHeader.get(groupPosition));
        return (children != null) ? children.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(listDataChild.get(listDataHeader.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Course course = (Course) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_group, parent, false);
        }

        MaterialTextView courseItemMTV = convertView.findViewById(R.id.courseItemMTV);
        courseItemMTV.setText(course.getTitle());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Section section = (Section) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_child, parent, false);
        }

        MaterialTextView sectionLabItemMTV = convertView.findViewById(R.id.sectionLabItemMTV);
        sectionLabItemMTV.setText(section.getName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
