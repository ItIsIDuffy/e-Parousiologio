package gr.ihu.eparousiologio.view.helper;

import gr.ihu.eparousiologio.model.Student;

public interface AccessStudentRecordActionsListener {
    void onTransfer(Student student);

    void onSwap(Student student);

    void onDelete(Student student);
}
