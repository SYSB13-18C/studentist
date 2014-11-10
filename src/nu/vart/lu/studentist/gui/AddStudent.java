package nu.vart.lu.studentist.gui;

import nu.vart.lu.studentist.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddStudent extends Page {
    protected JTextField id;
    protected JTextField name;
    protected Add add;

    public AddStudent(GUI gui) {
        super(gui);
        setLayout(new GridLayout(0, 2));
        id = new JTextField();
        add(new JLabel("ID"));
        add(id);
        name = new JTextField();
        add(new JLabel("Name"));
        add(name);
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
                nu.vart.lu.studentist.models.Student student = new nu.vart.lu.studentist.models.Student(id.getText(), name.getText());
                student.validate();
                studentist.add(student);
                gui.setComponent(new Student(gui, student));
                gui.feedback.add("Student " + id.getText() + " - " + name.getText() + " added.");
            } catch (Model.DuplicateKeyException e) {
                gui.feedback.add("A student with ID " + id.getText() + " already exists.");
            } catch (nu.vart.lu.studentist.models.Student.NameTooShortException e) {
                gui.feedback.add("Student name is too short (minimum 1 character).");
            } catch (nu.vart.lu.studentist.models.Student.IdTooShortException e) {
                gui.feedback.add("Student id is too short (minimum 1 character).");
            }
        }
    }
}
