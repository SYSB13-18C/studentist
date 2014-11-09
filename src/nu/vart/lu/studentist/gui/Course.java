package nu.vart.lu.studentist.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Course extends Page {
    protected nu.vart.lu.studentist.models.Course course;
    public Course(GUI gui, nu.vart.lu.studentist.models.Course course) {
        super(gui);
        this.course = course;
        add(new JLabel("course page for" + course.getCode()));
        add(new Remove());
    }

    protected class Remove extends JButton implements ActionListener {
        public Remove() {
            super("Remove");
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            studentist.remove(course);
            gui.feedback.add("Course " + course.getCode() + " - " + course.getName() + " (" + course.getPoints() + " points) removed.");
            gui.setComponent(gui.courses);
            gui.courses.search();
        }
    }
}
