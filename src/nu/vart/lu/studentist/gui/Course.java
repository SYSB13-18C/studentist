package nu.vart.lu.studentist.gui;

import nu.vart.lu.studentist.Model;
import nu.vart.lu.studentist.models.Studied;
import nu.vart.lu.studentist.models.Studies;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Course extends Page {
    protected nu.vart.lu.studentist.models.Course course;
    protected nu.vart.lu.studentist.models.Studies[] studies;
    protected nu.vart.lu.studentist.models.Studied[] studied;
    protected StudiedTable studiedTable;
    protected StudiesTable studiesTable;

    public Course(GUI gui, nu.vart.lu.studentist.models.Course course) {
        super(gui);
        this.course = course;
        setLayout(new GridLayout(0, 1));
        add(new GUI.Title(course.getCode() + " " + course.getName() + " (" + course.getPoints() + " points)"), BorderLayout.NORTH);
        JPanel sections = new JPanel(new GridLayout(0, 1));
        studies = studentist.getStudies(course);
        studied = studentist.getStudied(course);
        studiesTable = new StudiesTable();
        studiedTable = new StudiedTable();
        sections.add(studiesTable);
        sections.add(studiedTable);
        sections.add(new Remove());
        add(sections, BorderLayout.CENTER);
    }

    protected class Remove extends JButton implements ActionListener {
        public Remove() {
            super("Remove");
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                studentist.remove(course);
                gui.feedback.add("Course " + course.getCode() + " - " + course.getName() + " (" + course.getPoints() + " points) removed.");
                gui.setComponent(gui.courses);
                gui.courses.search();
            } catch (Model.HasRelationsException e) {
                gui.feedback.add("Course " + course.getCode() + " - " + course.getName() + " (" + course.getPoints() + " points) has relations (remove them first).");
            }
        }
    }


    protected class StudiedTable extends JPanel {
        public StudiedTable() {
            super(new GridLayout(0, 1));
            add(new GUI.Title(studied.length + " previous student(s).", 24));
            for (int i = 0; i < studied.length; i++)
                add(new Record(studied[i]));
        }

        protected class Record extends JPanel {
            protected Studied studied;

            public Record(Studied studied) {
                super(new GridLayout(1, 0));
                this.studied = studied;
                add(new JLabel(studied.getStudent().getId()));
                add(new JLabel(studied.getStudent().getName()));
                add(new JLabel(studied.getGrade()));
                add(new Remove());
            }

            protected class Remove extends JButton implements ActionListener {
                public Remove() {
                    super("X");
                    addActionListener(this);
                }

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    studentist.remove(studied);
                    gui.setComponent(new Course(gui, course));
                }
            }
        }
    }

    protected class StudiesTable extends JPanel {
        public StudiesTable() {
            super(new GridLayout(0, 1));
            add(new GUI.Title(studies.length + " current student(s).", 24));
            for (int i = 0; i < studies.length; i++)
                add(new Record(studies[i]));
            add(new Assigner());
        }

        protected class Record extends JPanel {
            protected Studies studies;
            protected Grader grader;
            protected String[] grades = { "I", "A", "B", "C", "D", "E", "U" };

            public Record(Studies studies) {
                super(new GridLayout(1, 0));
                this.studies = studies;
                add(new JLabel(studies.getStudent().getId()));
                add(new JLabel(studies.getStudent().getName()));
                grader = new Grader();
                add(new Grader());
                add(new Remove());
            }

            protected class Grader extends JComboBox<String> implements ActionListener {
                public Grader() {
                    super(grades);
                    addActionListener(this);
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    String grade = (String)getSelectedItem();
                    if (grade != "I") {
                        studentist.completeCourse(studies, grade);
                        gui.setComponent(new Course(gui, course));
                    }
                }
            }

            protected class Remove extends JButton implements ActionListener {
                public Remove() {
                    super("X");
                    addActionListener(this);
                }

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    studentist.remove(studies);
                    gui.setComponent(new Course(gui, course));
                }
            }
        }

        protected class Assigner extends JPanel {
            protected nu.vart.lu.studentist.models.Student[] availableStudents;

            public Assigner() {
                super(new GridLayout(1, 0));
                add(new JLabel("Assign to Student : "));
                availableStudents = studentist.getAvailableStudents(course);
                add(new StudentChooser());
            }

            private class StudentChooser extends JComboBox<nu.vart.lu.studentist.models.Student> implements ActionListener, ListCellRenderer<nu.vart.lu.studentist.models.Student> {
                private StudentChooser() {
                    super(availableStudents);
                    addActionListener(this);
                    setRenderer(this);
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        studentist.assign((nu.vart.lu.studentist.models.Student)getSelectedItem(), course);
                        gui.setComponent(new Course(gui, course));
                    } catch (Studies.AlreadyStudiesException e1) {
                        gui.feedback.add(e1.getMessage());
                        e1.printStackTrace();
                    }
                }

                @Override
                public Component getListCellRendererComponent(JList<? extends nu.vart.lu.studentist.models.Student> jList, nu.vart.lu.studentist.models.Student student, int i, boolean b, boolean b2) {
                    // This gets called with index -1 if jList is empty.
                    if (i < 0) return new JLabel("");
                    return new JLabel(student.getId() + " : " + student.getName());
                }
            }
        }
    }
}
