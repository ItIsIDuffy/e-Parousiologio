package gr.ihu.eparousiologio.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.adapter.SearchStudentsAdapter;
import gr.ihu.eparousiologio.model.LiveCourse;
import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.repository.CourseResubstitutionLogNoteSectionDAO;
import gr.ihu.eparousiologio.repository.StudentRecordsRepositoryDAO;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.OnResultListener;
import gr.ihu.eparousiologio.view.MainActivity;

public class ResubstitutionFragment extends Fragment {

    private static final String ARG_LIVE_COURSE = "liveCourse";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private CourseResubstitutionLogNoteSectionDAO courseSectionDAO;
    private StudentRecordsRepositoryDAO repository;
    private SearchStudentsAdapter adapter;
    private SearchView searchView;
    private Runnable searchRunnable;
    private MaterialTextView noStudentsFoundMTV;
    private LiveCourse liveCourse;

    public static ResubstitutionFragment newInstance(LiveCourse liveCourse) {
        ResubstitutionFragment fragment = new ResubstitutionFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LIVE_COURSE, liveCourse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            liveCourse = getArguments().getParcelable(ARG_LIVE_COURSE);
        }
        repository = new StudentRecordsRepositoryDAO();
        courseSectionDAO = new CourseResubstitutionLogNoteSectionDAO();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_resubstitution, container, false);

        searchView = root.findViewById(R.id.studentSearchView);
        RecyclerView recyclerView = root.findViewById(R.id.studentsSearchResultsRV);
        noStudentsFoundMTV = root.findViewById(R.id.noStudentsFoundMTV);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SearchStudentsAdapter(liveCourse, new ArrayList<>(), student -> courseSectionDAO.addResubstitutionNoteOnCourse(
                liveCourse.getCourseId(),
                student.getStudentAEM(),
                liveCourse.getLabId(),
                liveCourse.getLabName(),
                new OnResultListener<>() {
                    @Override
                    public void onSuccess(Void result) {
                        ((MainActivity) requireActivity()).resetToMainMenu();
                        CustomToast.showSuccess(requireActivity(),
                                "O " + student.getFullName() + " καταχωρήθηκε προς αναπλήρωση.");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        String msg = e.getMessage() != null ? e.getMessage().trim() : "";
                        ((MainActivity) requireActivity()).resetToMainMenu();
                        if (msg.contains("ανήκει ήδη στο τρέχον τμήμα")) {
                            CustomToast.showWarning(requireActivity(),
                                    "O " + student.getFullName() + " ανήκει ήδη στο τρέχον τμήμα.");
                        } else if (msg.contains("δεν ανήκει σε κανένα εργαστήριο")) {
                            CustomToast.showWarning(requireActivity(),
                                    "O " + student.getFullName() + " δεν ανήκει σε κανένα εργαστήριο.");
                        } else if (msg.contains("Υπάρχει ήδη αναπλήρωση")) {
                            CustomToast.showWarning(requireActivity(),
                                    "O " + student.getFullName() + " έχει ήδη δηλωθεί προς αναπλήρωση.");
                        } else {
                            CustomToast.showError(requireActivity(),
                                    "Αποτυχία προσθήκης σημείωσης αναπλήρωσης");
                        }
                    }
                }));
        recyclerView.setAdapter(adapter);

        setupSearch();

        return root;
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performDebouncedSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performDebouncedSearch(newText);
                return true;
            }
        });
    }

    private void performDebouncedSearch(String text) {
        if (searchRunnable != null) handler.removeCallbacks(searchRunnable);

        searchRunnable = () -> {
            String query = text.trim();

            if (query.isEmpty()) {
                adapter.updateList(new ArrayList<>());
                noStudentsFoundMTV.setVisibility(View.GONE);
                return;
            }

            repository.searchStudentsByAemPrefix(query, new OnResultListener<>() {
                @Override
                public void onSuccess(List<Student> result) {
                    adapter.updateList(result);

                    if (result.isEmpty()) {
                        noStudentsFoundMTV.setVisibility(View.VISIBLE);
                    } else {
                        noStudentsFoundMTV.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    noStudentsFoundMTV.setVisibility(View.VISIBLE);
                    noStudentsFoundMTV.setText("Παρουσιάστηκε σφάλμα κατά την αναζήτηση.");
                }
            });
        };

        handler.postDelayed(searchRunnable, 350);
    }
}
