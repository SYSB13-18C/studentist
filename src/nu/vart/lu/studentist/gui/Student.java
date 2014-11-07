package nu.vart.lu.studentist.gui;

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
        add(new JLabel(student.getName() + " " + student.getId()), BorderLayout.NORTH);
        JPanel sections = new JPanel(new GridLayout(0, 1));
        sections.add(studiesTable);
        sections.add(studiedTable);
        add(sections, BorderLayout.CENTER);
    }

    protected class StudiedTable extends JPanel {
        public StudiedTable() {
            super(new GridLayout(0, 1));
            add(new JLabel("Completed " + studied.length + " course(s)."));
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
                }
            }
        }
    }

    protected class StudiesTable extends JPanel {
        public StudiesTable() {
            super(new GridLayout(0, 1));
            add(new JLabel("Currently studying " + studies.length + " course(s)."));
            for (int i = 0; i < studies.length; i++)
                add(new Record(studies[i]));
        }

        protected class Record extends JPanel {
            protected Studies studies;
            protected Grader grader;
            protected String[] grades = { "I", "A", "B", "C", "D", "E", "U" };

            public Record(Studies studies) {
                super(new GridLayout(1, 0));
                this.studies = studies;
                add(new JLabel(studies.getCourse().getCode()));
                add(new JLabel(studies.getCourse().getName()));
                add(new JLabel("" + studies.getCourse().getPoints()));
                grader = new Grader();
                add(new Grader());
            }

            protected class Grader extends JComboBox<String> implements ActionListener {
                public Grader() {
                    super(grades);
                    addActionListener(this);
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("action perf00rmed");
                    System.out.println(e.getSource());
                    System.out.println(getSelectedItem());
                    String grade = (String)getSelectedItem();
                    if (grade != "I") {
                        studentist.completeCourse(studies, grade);
                        gui.setComponent(new Student(gui, student));
                    }
                }
            }
        }
    }
}
