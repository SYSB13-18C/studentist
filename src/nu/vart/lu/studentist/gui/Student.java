package nu.vart.lu.studentist.gui;

import javax.swing.*;

public class Student extends Page {
    protected nu.vart.lu.studentist.models.Student student;

    public Student(GUI gui, nu.vart.lu.studentist.models.Student student) {
        super(gui);
        this.student = student;
        add(new JLabel("Student" + student.getId()));
    }
}
