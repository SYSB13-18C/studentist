package nu.vart.lu.studentist.gui;

import nu.vart.lu.studentist.Studentist;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    protected Studentist studentist;
    public Studentist getStudentist() { return studentist; }
    protected Menu menu;
    public Feedback feedback;
    protected JPanel container = new JPanel(); // for the activities, or something
    protected JScrollPane pane;
    public final static Border EmptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);

    // pages / views
    protected Courses courses;
    protected Students students;
    protected AddStudent addStudent;
    protected AddCourse addCourse;

    public GUI(Studentist studentist) {
        this.studentist = studentist;
        setTitle(studentist.title);
        setLayout(new BorderLayout());
        this.setSize(800, 600);

        // container, for activities
        container.setLayout(new GridLayout(1, 1));
        pane = new JScrollPane(container,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setBorder(EmptyBorder);
        pane.setViewportView(container);
        add(pane, BorderLayout.CENTER);

        // pages
        courses = new Courses(this);
        students = new Students(this);
        addStudent = new AddStudent(this);
        addCourse = new AddCourse(this);

        // menu
        menu = new Menu();
        add(menu, BorderLayout.NORTH);

        // feedback
        feedback = new Feedback();
        add(feedback, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Force redraw of container.
     * TODO remove? (shouldn't be needed)
     */
    public void redraw() {
        // be JRE6 compliant, don't use revalidate()
        container.invalidate();
        container.validate();
        container.repaint();
    }

    /**
     * Direct access to set a JComponent in the main container.
     *
     * @param component The JComponent.
     */
    public void setComponent(JComponent component) {
        if (!(container.getComponentCount() > 0 && component == container.getComponent(0))) {
            container.removeAll();
            container.add(component);
        }
        pane.getViewport().revalidate();
        pane.setPreferredSize(new Dimension(container.getPreferredSize().width, component.getPreferredSize().height));
        redraw();
    }

    /**
     * Just a menu...
     */
    protected class Menu extends JPanel {
        /**
         * Construct that menu...
         */
        public Menu() {
            setLayout(new GridLayout(1, 0));
            add(new Navigation("Students", students));
            add(new Navigation("Courses", courses));
        }

        /**
         * Navigate to stuff.
         */
        protected class Navigation extends JButton implements ActionListener {
            protected JComponent target;

            public Navigation(String label, JComponent target) {
                super(label);
                this.target = target;
                addActionListener(this);
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setComponent(target);
            }
        }
    }

    /**
     * User feedback.
     */
    public class Feedback extends JPanel {
        protected JButton clear = new Clear();
        protected JPanel messages = new JPanel(new GridLayout(0, 1));

        public Feedback() {
            super(new BorderLayout());
            add(clear, BorderLayout.EAST);
            add(messages, BorderLayout.CENTER);
        }

        public void add(String message) {
            JLabel label = new JLabel(message);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            messages.add(label);
        }

        private class Clear extends JButton implements ActionListener {
            private Clear() {
                super("X");
                addActionListener(this);
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                messages.removeAll();
                revalidate();
            }
        }
    }

    public static class Title extends JLabel {
        public Title(String text) {
            this(text, 30);
        }

        public Title(String text, int size) {
            super(text);
            setFont(new Font("MonoType Corsiva", Font.PLAIN, size));
            setHorizontalAlignment(JLabel.CENTER);
        }
    }
}
