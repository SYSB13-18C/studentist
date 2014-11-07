package nu.vart.lu.studentist.gui;

import nu.vart.lu.studentist.models.Studied;
import nu.vart.lu.studentist.models.Studies;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

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
                this.studied = studied;
                add(new JLabel(studied.getCourse().getCode() + " " + studied.getCourse().getName() + " " + studied.getCourse().getPoints() + " " + studied.getGrade()));

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
            protected Studied studied;

            public Record(Studies studies) {
                super(new GridLayout());
                this.studied = studied;
                add(new JLabel(studies.getCourse().getCode() + " " + studies.getCourse().getName() + " " + studies.getCourse().getPoints()));
            }
        }
    }
}
