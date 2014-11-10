package nu.vart.lu.studentist.gui;

import nu.vart.lu.studentist.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddCourse extends Page {
    protected JTextField code;
    protected JTextField name;
    protected JTextField points;
    protected Add add;

    public AddCourse(GUI gui) {
        super(gui);
        setLayout(new GridLayout(0, 2));
        code = new JTextField();
        add(new JLabel("ID"));
        add(code);
        name = new JTextField();
        add(new JLabel("Name"));
        add(name);
        points = new JTextField();
        add(new JLabel("Points"));
        add(points);
        add = new Add();
        add(new JLabel(""));
        add(add);
    }

    protected class Add extends JButton implements ActionListener {
        public Add() {
            super("Add");
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                int thePoints = Integer.parseInt(points.getText());
                nu.vart.lu.studentist.models.Course course = new nu.vart.lu.studentist.models.Course(code.getText(), name.getText(), thePoints);
                studentist.add(course);
                gui.setComponent(new Course(gui, course));
                gui.feedback.add("Course " + code.getText() + " - " + name.getText() + " (" + thePoints + " points) added.");
            } catch (NumberFormatException e) {
                gui.feedback.add("Points has to be a number.");
            } catch (Model.DuplicateKeyException e) {
                gui.feedback.add("A course with ID " + code.getText() + " already exists.");
            } catch (Model.InvalidValueException e) {
                gui.feedback.add("Oh noes! " + e.getMessage());
            }
        }
    }
}
