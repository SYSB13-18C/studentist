package nu.vart.lu.studentist.gui;

import nu.vart.lu.studentist.Model;
import nu.vart.lu.studentist.models.Studied;
import nu.vart.lu.studentist.models.Studies;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class Course extends Page {
    protected nu.vart.lu.studentist.models.Course course;
    protected nu.vart.lu.studentist.models.Studies[] studies;
    protected nu.vart.lu.studentist.models.Studied[] studied;
    protected Statistics statistics;
    protected StudiedTable studiedTable;
    protected StudiesTable studiesTable;

    public Course(GUI gui, nu.vart.lu.studentist.models.Course course) {
        super(gui);
        this.course = course;
        setLayout(new BorderLayout());
        add(new GUI.Title(course.getCode() + " : " + course.getName() + " (" + course.getPoints() + " points)"), BorderLayout.NORTH);
        JPanel sections = new JPanel(new GridLayout(0, 1));
        studies = studentist.getStudies(course);
        studied = studentist.getStudied(course);
        statistics = new Statistics();
        studiesTable = new StudiesTable();
        studiedTable = new StudiedTable();
        sections.add(studiesTable);
        sections.add(studiedTable);
        sections.add(statistics);
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

    protected class Statistics extends JPanel {
        protected float throughput = 0;
        protected int aCount = 0;
        protected float aPercent = 0;
        protected int bCount = 0;
        protected float bPercent = 0;
        protected int cCount = 0;
        protected float cPercent = 0;
        protected int dCount = 0;
        protected float dPercent = 0;
        protected int eCount = 0;
        protected float ePercent = 0;
        protected int uCount = 0;
        protected float uPercent = 0;

        protected DecimalFormat format = new DecimalFormat("#.##");

        public Statistics() {
            super(new GridLayout(0, 1));

            // calculate
            if (studied.length > 0) {
                Float length = (float)studied.length;
                for (int i = 0; i < studied.length; i++) {
                    if (studied[i].getGrade().compareTo("A") == 0)
                        aCount++;
                    else if (studied[i].getGrade().compareTo("B") == 0)
                        bCount++;
                    else if (studied[i].getGrade().compareTo("C") == 0)
                        cCount++;
                    else if (studied[i].getGrade().compareTo("D") == 0)
                        dCount++;
                    else if (studied[i].getGrade().compareTo("E") == 0)
                        eCount++;
                    else if (studied[i].getGrade().compareTo("U") == 0)
                        uCount++;
                }

                aPercent = Float.valueOf(format.format(aCount / length * 100));
                bPercent = Float.valueOf(format.format(bCount / length * 100));
                cPercent = Float.valueOf(format.format(cCount / length * 100));
                dPercent = Float.valueOf(format.format(dCount / length * 100));
                ePercent = Float.valueOf(format.format(eCount / length * 100));
                uPercent = Float.valueOf(format.format(uCount / length * 100));

                throughput = Float.valueOf(format.format((length - uCount) / length * 100));
            }

            // display
            add(new Value(throughput + "% throughput"));
            add(new Value(aPercent + "% A"));
            add(new Value(bPercent + "% B"));
            add(new Value(cPercent + "% C"));
            add(new Value(dPercent + "% D"));
            add(new Value(ePercent + "% E"));
            add(new Value(uPercent + "% U"));
        }

        private class Value extends JLabel {
            public Value(String string) {
                super(string);
                setHorizontalAlignment(JLabel.CENTER);
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
                add(new JLabel(studied.getSemester()));
                add(new JLabel("" + studied.getCourse().getPoints()));
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
            protected String[] grades = { "Incomplete", "A", "B", "C", "D", "E", "U" };

            public Record(Studies studies) {
                super(new GridLayout(1, 0));
                this.studies = studies;
                add(new JLabel(studies.getStudent().getId()));
                add(new JLabel(studies.getStudent().getName()));
                add(new JLabel(studies.getSemester()));
                add(new JLabel("" + studies.getCourse().getPoints()));
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
            protected StudentChooser studentChooser;
            protected SemesterChooser semesterChooser;
            // TODO no hard-code, kthx
            protected String[] availableSemesters = { "2014 Fall", "2015 Spring", "2015 Fall", "2016 Spring" };

            public Assigner() {
                super(new GridLayout(1, 0));
                add(new JLabel("Assign to Student : "));
                availableStudents = studentist.getAvailableStudents(course);
                semesterChooser = new SemesterChooser();
                studentChooser = new StudentChooser();
                add(studentChooser);
                add(semesterChooser);
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
                        studentist.assign((nu.vart.lu.studentist.models.Student)getSelectedItem(), course, (String)semesterChooser.getSelectedItem());
                        gui.setComponent(new Course(gui, course));
                    }
                    catch (Studies.AlreadyStudiesException e1) {
                        gui.feedback.add(e1.getMessage());
                    }
                    catch (Studies.MaxPointsException e1) {
                        gui.feedback.add("A Student may study a maximum of 45 points per semester.");
                    }
                }

                @Override
                public Component getListCellRendererComponent(JList<? extends nu.vart.lu.studentist.models.Student> jList, nu.vart.lu.studentist.models.Student student, int i, boolean b, boolean b2) {
                    // This gets called with index -1 if jList is empty.
                    if (i < 0) return new JLabel("");
                    return new JLabel(student.getId() + " : " + student.getName());
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
