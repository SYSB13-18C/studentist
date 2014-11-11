package nu.vart.lu.studentist.gui;

import nu.vart.lu.studentist.Model;
import nu.vart.lu.studentist.models.Course;
import nu.vart.lu.studentist.models.Studied;
import nu.vart.lu.studentist.models.Studies;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Student extends Page {
    protected nu.vart.lu.studentist.models.Student student;
    protected nu.vart.lu.studentist.models.Studies[] studies;
    protected nu.vart.lu.studentist.models.Studied[] studied;
    protected StudiedTable studiedTable;
    protected StudiesTable studiesTable;

    public Student(GUI gui, nu.vart.lu.studentist.models.Student student) {
        super(gui);
        setLayout(new BorderLayout());
        this.student = student;
        studies = studentist.getStudies(student);
        studied = studentist.getStudied(student);
        studiesTable = new StudiesTable();
        studiedTable = new StudiedTable();
        add(new GUI.Title(student.getId() + " : " + student.getName()), BorderLayout.NORTH);
        JPanel sections = new JPanel(new GridLayout(0, 1));
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
                studentist.remove(student);
                gui.feedback.add("Student " + student.getId() + " - " + student.getName() + " removed.");
                gui.students.search();
                gui.setComponent(gui.students);
            } catch (Model.HasRelationsException e) {
                gui.feedback.add("Student " + student.getId() + " - " + student.getName() + " has relations (remove them first).");
            }
        }
    }

    protected class StudiedTable extends JPanel {
        public StudiedTable() {
            super(new GridLayout(0, 1));
            add(new GUI.Title("Completed " + studied.length + " course(s).", 24));
            for (int i = 0; i < studied.length; i++)
                add(new Record(studied[i]));
        }

        protected class Record extends JPanel {
            protected Studied studied;

            public Record(Studied studied) {
                super(new GridLayout(1, 0));
                this.studied = studied;
                add(new JLabel(studied.getCourse().getCode()));
                add(new JLabel(studied.getCourse().getName()));
                add(new JLabel("" + studied.getCourse().getPoints()));
                add(new JLabel(studied.getGrade()));
                add(new JLabel(studied.getSemester()));
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
                    gui.setComponent(new Student(gui, student));
                    gui.feedback.add("Student " + studied.getStudent().getId() + " removed from course " + studied.getCourse().getCode() + " (studied)");
                }
            }
        }
    }

    protected class StudiesTable extends JPanel {
        public StudiesTable() {
            super(new GridLayout(0, 1));
            add(new GUI.Title("Currently studying " + studies.length + " course(s).", 24));
            for (int i = 0; i < studies.length; i++)
                add(new Record(studies[i]));
            add(new Assigner());
        }

        protected class Record extends JPanel {
            protected Studies studies;
            protected Grader grader;
            protected String[] grades = { "Incomplete", "A", "B", "C", "D", "E", "U" };

            public Record(Studies studies) {
                super(new GridLayout(1, 0));
                this.studies = studies;
                add(new JLabel(studies.getCourse().getCode()));
                add(new JLabel(studies.getCourse().getName()));
                add(new JLabel("" + studies.getCourse().getPoints()));
                add(new JLabel(studies.getSemester()));
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
                    if (grade != "Incomplete") {
                        studentist.completeCourse(studies, grade);
                        gui.setComponent(new Student(gui, student));
                        gui.feedback.add("Student " + studies.getStudent().getId() + " graded " + grade + " on course " + studies.getCourse().getCode());
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
                    gui.setComponent(new Student(gui, student));
                    gui.feedback.add("Student " + student.getId() + " removed from course " + studies.getCourse().getCode() + " (studies)");
                }
            }
        }

        protected class Assigner extends JPanel {
            protected Course[] availableCourses;
            protected CourseChooser courseChooser;
            protected SemesterChooser semesterChooser;
            // TODO no hard-code, kthx
            protected String[] availableSemesters = { "2014 Fall", "2015 Spring", "2015 Fall", "2016 Spring" };

            public Assigner() {
                super(new GridLayout(1, 0));
                add(new JLabel("Assign Course : "));
                availableCourses = studentist.getAvailableCourses(student);
                semesterChooser = new SemesterChooser();
                courseChooser = new CourseChooser();
                add(courseChooser);
                add(semesterChooser);
            }

            protected class CourseChooser extends JComboBox<Course> implements ActionListener, ListCellRenderer<Course> {
                protected CourseChooser() {
                    super(availableCourses);
                    addActionListener(this);
                    setRenderer(this);
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Course course = (Course)getSelectedItem();
                        studentist.assign(student, course, (String)semesterChooser.getSelectedItem());
                        gui.setComponent(new Student(gui, student));
                        gui.feedback.add("Student " + student.getId() + " assigned to " + course.getCode());
                    }
                    catch (Studies.AlreadyStudiesException e1) {
                        gui.feedback.add(e1.getMessage());
                    }
                    catch (Studies.MaxPointsException e1) {
                        gui.feedback.add("A Student may study a maximum of 45 points per semester.");
                    }
                }

                @Override
                public Component getListCellRendererComponent(JList<? extends Course> jList, Course course, int i, boolean b, boolean b2) {
                    // This gets called with index -1 if jList is empty.
                    if (i < 0) return new JLabel("");
                    return new JLabel(course.getCode() + " : " + course.getName() + " (" + course.getPoints() + " points)");
                }
            }

            protected class SemesterChooser extends JComboBox<String> {
                protected SemesterChooser() {
                    super(availableSemesters);
                }
            }
        }
    }
}
