package gr.ihu.eparousiologio.view.fragment;

import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.repository.StudentRecordsRepositoryDAO;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.OnResultListener;

public class AddStudentSheet extends BottomSheetDialogFragment {

    TextInputLayout addStudentAEMTIL, addStudentSemesterTIL, addStudentFullNameTIL;
    TextInputEditText addStudentAEMTIET, addStudentSemesterTIET, addStudentFullNameTIET;
    MaterialButton addStudentMB;

    MaterialTextView addStudentCourseMTV, addStudentSectionMTV;
    View rootView;

    Course course;
    Section section;

    public AddStudentSheet(Course course, Section section) {
        this.course = course;
        this.section = section;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_student_sheet, container, false);

        initializeUI();

        setUpTextWatcher(addStudentAEMTIET);
        setUpTextWatcher(addStudentSemesterTIET);
        setUpTextWatcher(addStudentFullNameTIET);

        addStudentSectionMTV.setText(getString(R.string.current_section, section.getName()));
        addStudentCourseMTV.setText(getString(R.string.current_course, course.getTitle()));

        addStudentMB.setOnClickListener(view -> {
            String aem = Objects.requireNonNull(addStudentAEMTIET.getText()).toString().trim();
            String semester = Objects.requireNonNull(addStudentSemesterTIET.getText()).toString().trim();
            String fullName = Objects.requireNonNull(addStudentFullNameTIET.getText()).toString().trim();

            if (aem.isEmpty()) {
                addStudentAEMTIL.setError(getString(R.string.field_required));
                addStudentAEMTIL.setErrorEnabled(true);
                return;
            }

            if (semester.isEmpty()) {
                addStudentSemesterTIL.setError(getString(R.string.field_required));
                addStudentSemesterTIL.setErrorEnabled(true);
                return;
            }

            if (fullName.isEmpty()) {
                addStudentFullNameTIL.setError(getString(R.string.field_required));
                addStudentFullNameTIL.setErrorEnabled(true);
                return;
            }

            Student student = new Student(fullName, aem, semester, true);

            if (course.getCourseId().isEmpty() || section.getLabId().isEmpty()) {
                CustomToast.showError(requireActivity(), "Το εργαστήριο επιστρέφει null");
                dismiss();
                return;
            }

            new StudentRecordsRepositoryDAO().addStudentToCourseSection(course.getCourseId(), section.getLabId(), student, new OnResultListener<>() {
                @Override
                public void onSuccess(Void result) {
                    CustomToast.showSuccess(requireActivity(), "Επιτυχής εγγραφή φοιτητή");
                    dismiss();
                }

                @Override
                public void onFailure(Exception e) {
                    CustomToast.showError(requireActivity(), e.getMessage());
                }
            });
        });

        return rootView;
    }

    private void setUpTextWatcher(TextInputEditText textInputEditText) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isErrorEnabled()) {
                    resetTILErrors();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
    }

    private void resetTILErrors() {
        addStudentAEMTIL.setError("");
        addStudentAEMTIL.setErrorEnabled(false);
        addStudentSemesterTIL.setError("");
        addStudentSemesterTIL.setErrorEnabled(false);
        addStudentFullNameTIL.setError("");
        addStudentFullNameTIL.setErrorEnabled(false);
    }

    private boolean isErrorEnabled() {
        return addStudentAEMTIL.isErrorEnabled() || addStudentSemesterTIL.isErrorEnabled() || addStudentFullNameTIL.isErrorEnabled();
    }

    private void initializeUI() {
        addStudentAEMTIL = rootView.findViewById(R.id.addStudentAEMTIL);
        addStudentSemesterTIL = rootView.findViewById(R.id.addStudentSemesterTIL);
        addStudentFullNameTIL = rootView.findViewById(R.id.addStudentFullNameTIL);
        addStudentAEMTIET = rootView.findViewById(R.id.addStudentAEMTIET);
        addStudentSemesterTIET = rootView.findViewById(R.id.addStudentSemesterTIET);
        addStudentFullNameTIET = rootView.findViewById(R.id.addStudentFullNameTIET);
        addStudentMB = rootView.findViewById(R.id.addStudentMB);
        addStudentSectionMTV = rootView.findViewById(R.id.addStudentSectionMTV);
        addStudentCourseMTV = rootView.findViewById(R.id.addStudentCourseMTV);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(d -> {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {

                float radius = getResources().getDimension(R.dimen.bottom_sheet_radius);
                int bgColor = ContextCompat.getColor(requireContext(), R.color.sheet_background);

                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setColor(bgColor);
                drawable.setCornerRadii(new float[]{
                        radius, radius,
                        radius, radius,
                        0f, 0f,
                        0f, 0f
                });

                bottomSheet.setBackground(drawable);
            }
        });

        return dialog;
    }

}
