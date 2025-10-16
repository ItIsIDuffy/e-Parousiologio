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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.model.LiveCourse;
import gr.ihu.eparousiologio.repository.CourseResubstitutionLogNoteSectionDAO;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.util.OnResultListener;

public class AddSectionNoteSheet extends BottomSheetDialogFragment {

    private final CourseResubstitutionLogNoteSectionDAO courseSectionDAO = new CourseResubstitutionLogNoteSectionDAO();
    private View rootView;
    private LiveCourse liveCourse;
    private MaterialTextView addSectionNoteCourseMTV, addSectionNoteSectionMTV;
    private MaterialButton addSectionNoteSubmitMB;
    private TextInputLayout addSectionNoteTIL;
    private TextInputEditText addSectionNoteTIET;

    public AddSectionNoteSheet(LiveCourse liveCourse) {
        this.liveCourse = liveCourse;
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
        rootView = inflater.inflate(R.layout.fragment_add_section_note, container, false);

        initializeUi();

        return rootView;
    }

    private void initializeUi() {
        addSectionNoteCourseMTV = rootView.findViewById(R.id.addSectionNoteCourseMTV);
        addSectionNoteSectionMTV = rootView.findViewById(R.id.addSectionNoteSectionMTV);
        addSectionNoteSubmitMB = rootView.findViewById(R.id.addSectionNoteSubmitMB);
        addSectionNoteTIL = rootView.findViewById(R.id.addSectionNoteTIL);
        addSectionNoteTIET = rootView.findViewById(R.id.addSectionNoteTIET);

        addSectionNoteCourseMTV.setText(liveCourse.getCourseTitle());
        addSectionNoteSectionMTV.setText(liveCourse.getLabName());

        setUpTextWatcher(addSectionNoteTIET);

        addSectionNoteSubmitMB.setOnClickListener(view -> {
            view.setEnabled(false);
            String noteText = Objects.requireNonNull(addSectionNoteTIET.getText()).toString().trim();

            if (noteText.isEmpty()) {
                addSectionNoteTIL.setError("Συμπληρώστε το πεδίο σημείωσης");
                addSectionNoteTIL.setErrorEnabled(true);
                view.setEnabled(true);
                return;
            }

            addSectionNoteTIL.setError(null);
            addSectionNoteTIL.setErrorEnabled(false);

            if (isAemLike(noteText)) {
                courseSectionDAO.addResubstitutionNoteOnCourse(
                        liveCourse.getCourseId(),
                        noteText,
                        liveCourse.getLabId(),
                        liveCourse.getLabName(),
                        new OnResultListener<>() {
                            @Override
                            public void onSuccess(Void result) {
                                addSectionNoteTIET.setText("");
                                dismiss();
                                CustomToast.showSuccess(requireActivity(), "O " + noteText + " καταχωρήθηκε προς αναπλήρωση.");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                String msg = e.getMessage() != null ? e.getMessage().trim() : "";

                                if (msg.contains("ανήκει ήδη στο τρέχον τμήμα")) {
                                    dismiss();
                                    CustomToast.showWarning(requireActivity(), "O " + noteText + " ανήκει ήδη στο τρέχον τμήμα. Καταχωρήστε παρουσία από τη λίστα παρουσιών.");
                                } else if (msg.contains("δεν ανήκει σε κανένα εργαστήριο")) {
                                    dismiss();
                                    CustomToast.showWarning(requireActivity(), "O " + noteText + " δεν ανήκει σε κανένα εργαστήριο.");
                                } else if (msg.contains("έχει ήδη δηλωθεί προς αναπλήρωση.")) {
                                    dismiss();
                                    CustomToast.showWarning(requireActivity(), "O " + noteText + " έχει ήδη δηλωθεί προς αναπλήρωση.");
                                } else {
                                    dismiss();
                                    CustomToast.showError(requireActivity(), "Αποτυχία προσθήκης σημείωσης αναπλήρωσης");
                                }
                            }
                        });
            } else {
                courseSectionDAO.addNoteOnCourse(
                        liveCourse.getCourseId(),
                        noteText,
                        new OnResultListener<>() {
                            @Override
                            public void onSuccess(Void result) {
                                addSectionNoteTIET.setText("");
                                dismiss();
                                CustomToast.showSuccess(requireActivity(), "Επιτυχής προσθήκη σημείωσης.");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                dismiss();
                                CustomToast.showError(requireActivity(), "Αποτυχία προσθήκης σημείωσης");
                            }
                        });
            }
        });

    }

    private boolean isAemLike(String text) {
        if (!text.matches("\\d{1,5}")) return false;
        try {
            int value = Integer.parseInt(text);
            return value >= 1 && value <= 99999;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void setUpTextWatcher(TextInputEditText textInputEditText) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (addSectionNoteTIL.isErrorEnabled()) {
                    addSectionNoteTIL.setError("");
                    addSectionNoteTIL.setErrorEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
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
