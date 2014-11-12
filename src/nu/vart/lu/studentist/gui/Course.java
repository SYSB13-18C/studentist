package nu.vart.lu.studentist.gui;

import nu.vart.lu.studentist.Model;
import nu.vart.lu.studentist.models.Studied;
import nu.vart.lu.studentist.models.Studies;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
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
    protected static GridBagLayout layout = new GridBagLayout() { };

    public Course(GUI gui, nu.vart.lu.studentist.models.Course course) {
        super(gui);
        this.course = course;
        studies = studentist.getStudies(course);
        studied = studentist.getStudied(course);

        setLayout(new BorderLayout());

        add(new GUI.Title(course.getName()), BorderLayout.NORTH);

        JPanel sections = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(15, 15, 15, 15);

        JPanel meta = new JPanel(new GridLayout(0, 1));
        meta.add(new JLabel("code    " + course.getCode()));
        meta.add(new JLabel("points  " + course.getPoints()));
        sections.add(meta, c);

        studiesTable = new StudiesTable();
        sections.add(studiesTable, c);

        studiedTable = new StudiedTable();
        sections.add(studiedTable, c);

        statistics = new Statistics();
        sections.add(statistics, c);

        sections.add(new Remove(), c);

        add(sections, BorderLayout.CENTER);
    }

    protected class Remove extends JButton implements ActionListener {
        public Remove() {
            super("Remove Course");
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

        protected Object[][] data;
        protected String[] headers = new String[] { "", "Count", "Percent" };

        public Statistics() {
            super(new BorderLayout());

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

                aPercent = format(aCount / length * 100);
                bPercent = format(bCount / length * 100);
                cPercent = format(cCount / length * 100);
                dPercent = format(dCount / length * 100);
                ePercent = format(eCount / length * 100);
                uPercent = format(uCount / length * 100);

                throughput = format((length - uCount) / length * 100);
            }

            // display
            add(new GUI.SubTitle("Statistics"), BorderLayout.NORTH);

            data = new Object[][] {
                    { "Throughput", studied.length - uCount, throughput },
                    { "A", aCount, aPercent },
                    { "B", bCount, bPercent },
                    { "C", cCount, cPercent },
                    { "D", dCount, dPercent },
                    { "E", eCount, ePercent },
                    { "U", uCount, uPercent } };

            JTable table = new JTable(data, headers);
            table.setModel(new TableModel() {
                @Override
                public int getRowCount() {
                    return data.length;
                }

                @Override
                public int getColumnCount() {
                    return headers.length;
                }

                @Override
                public String getColumnName(int i) {
                    return headers[i];
                }

                @Override
                public Class<?> getColumnClass(int i) {
                    return String.class;
                }

                @Override
                public boolean isCellEditable(int i, int i2) {
                    return false;
                }

                @Override
                public Object getValueAt(int i, int i2) {
                    return data[i][i2];
                }

                @Override
                public void setValueAt(Object o, int i, int i2) {
                    data[i][i2] = o;
                }

                @Override
                public void addTableModelListener(TableModelListener tableModelListener) {

                }

                @Override
                public void removeTableModelListener(TableModelListener tableModelListener) {

                }
            } );

            JPanel container = new JPanel(new BorderLayout());
            container.add(table.getTableHeader(), BorderLayout.NORTH);
            container.add(table, BorderLayout.CENTER);
            add(container, BorderLayout.CENTER);
        }

        protected float format(float f) {
            return ((float)(int)(f * 100)) / 100;
        }
    }

    protected class StudiedTable extends JPanel {
        public StudiedTable() {
            super(new GridLayout(0, 1));

            add(new GUI.SubTitle(studied.length + " previous Student(s)"));

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
                    gui.feedback.add("Student " + studied.getStudent().getId() + " removed from course " + course.getCode());
                }
            }
        }
    }

    protected class StudiesTable extends JPanel {
        public StudiesTable() {
            super(new GridLayout(0, 1));
            add(new GUI.SubTitle(studies.length + " current Student(s)"));
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
                        gui.feedback.add("Student " + studies.getStudent().getId() + " graded " + grade + " on course " + course.getCode());
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
                    gui.feedback.add("Student " + studies.getStudent().getId() + " unassigned from course " + course.getCode());
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
                        nu.vart.lu.studentist.models.Student student = (nu.vart.lu.studentist.models.Student)getSelectedItem();
                        studentist.assign(student, course, (String)semesterChooser.getSelectedItem());
                        gui.setComponent(new Course(gui, course));
                        gui.feedback.add("Student " + student.getId() + " assigned to course " + course.getCode());
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
