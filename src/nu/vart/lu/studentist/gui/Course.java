package nu.vart.lu.studentist.gui;

import javax.swing.*;

/**
 * Created by zarac on 11/9/14.
 */
public class Course extends Page {
    protected nu.vart.lu.studentist.models.Course course;
    public Course(GUI gui, nu.vart.lu.studentist.models.Course course) {
        super(gui);
        this.course = course;
        add(new JLabel("course page for" + course.getCode()));
    }
}
