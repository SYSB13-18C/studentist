package nu.vart.lu.studentist.gui;

import nu.vart.lu.studentist.Studentist;
import nu.vart.lu.studentist.models.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Students extends Page {
    protected JButton add = new Add();
    protected JPanel top = new JPanel(new GridLayout(1, 0));
    protected JTextField input = new Input();
    protected Result result = new Result();

    public Students(GUI gui) {
        super(gui);
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        top.add(add);
        top.add(input);
        add(result, BorderLayout.CENTER);
        search("");
    }

    public void search() {
        search(input.getText());
    }

    public void search(String query) {
        result.set(studentist.database.getStudents(query));
    }

    protected class Result extends JPanel {
        protected nu.vart.lu.studentist.models.Student[] students = new nu.vart.lu.studentist.models.Student[0];

        public Result() {
            setLayout(new GridLayout(0, 1));
            add(new JLabel("no result yet"));
            set(students);
        }

        public void set(nu.vart.lu.studentist.models.Student[] students) {
            this.students = students;
            removeAll();
            add(new GUI.Title("Found " + students.length + " students."));
            for (int i = 0; i < students.length; i++)
                add(new Student(students[i]));
            revalidate();
        }
    }

    protected class Input extends JTextField implements KeyListener {
        public Input() {
            addKeyListener(this);
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) { }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            search(getText());
        }

        @Override
        public void keyTyped(KeyEvent keyEvent) { }
    }

    public class Student extends JPanel implements ActionListener {
        protected nu.vart.lu.studentist.models.Student student;
        protected JButton id;
        protected JLabel name;

        public Student(nu.vart.lu.studentist.models.Student student) {
            super(new GridLayout(0, 2));
            this.student = student;
            id = new JButton(student.getId());
            name = new JLabel(student.getName());
            add(id);
            add(name);
            id.addActionListener(this);
            //name.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            gui.setComponent(new nu.vart.lu.studentist.gui.Student(gui, student));
        }
    }

    private class Add extends JButton implements ActionListener {
        public Add() {
            super("+");
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            gui.setComponent(gui.addStudent);
        }
    }
}