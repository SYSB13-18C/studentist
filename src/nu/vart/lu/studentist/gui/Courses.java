package nu.vart.lu.studentist.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Courses extends Page {
    protected JButton add = new Add();
    protected JPanel top = new JPanel(new GridLayout(1, 0));
    protected JTextField input = new Input();
    protected Result result = new Result();

    public Courses(GUI gui) {
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
        result.set(studentist.database.getCourses(query));
    }

    protected class Add extends JButton implements ActionListener {
        public Add() {
            super("+ New Course");
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            gui.setComponent(gui.addCourse);
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

    protected class Result extends JPanel {
        protected nu.vart.lu.studentist.models.Course[] courses = new nu.vart.lu.studentist.models.Course[0];

        public Result() {
            setLayout(new GridLayout(0, 1));
            add(new JLabel("no result yet"));
            set(courses);
        }

        public void set(nu.vart.lu.studentist.models.Course[] courses) {
            this.courses = courses;
            removeAll();
            add(new GUI.Title("Found " + courses.length + " course(s).", 20));
            for (int i = 0; i < courses.length; i++)
                add(new Record(courses[i]));
            revalidate();
        }

        public class Record extends JPanel implements ActionListener {
            protected nu.vart.lu.studentist.models.Course course;
            protected JButton id;
            protected JLabel name;
            protected JLabel points;

            public Record(nu.vart.lu.studentist.models.Course course) {
                super(new GridLayout(0, 3));
                this.course = course;
                id = new JButton(course.getCode());
                name = new JLabel(course.getName());
                points = new JLabel("" + course.getPoints());
                add(id);
                add(name);
                add(points);
                id.addActionListener(this);
                //name.addActionListener(this);
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gui.setComponent(new nu.vart.lu.studentist.gui.Course(gui, course));
            }
        }
    }
}