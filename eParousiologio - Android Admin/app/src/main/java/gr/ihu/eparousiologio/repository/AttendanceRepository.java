package gr.ihu.eparousiologio.repository;

import java.util.List;
import java.util.Map;

import gr.ihu.eparousiologio.model.AttendanceEntry;
import gr.ihu.eparousiologio.util.OnResultListener;

public interface AttendanceRepository {
    void fetchCourseAttendanceOnce(String courseId, OnResultListener<AttendanceSnapshot> listener);

    class AttendanceSnapshot {
        public Map<String, Integer> sessionsByLab;
        public Map<String, Map<String, List<AttendanceEntry>>> entriesByLabAndAem;
    }
}