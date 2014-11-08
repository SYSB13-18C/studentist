package nu.vart.lu.studentist.gui;

import nu.vart.lu.studentist.Studentist;

import javax.swing.*;
import java.awt.*;

public class Page extends JPanel {
    protected Studentist studentist;
    protected GUI gui;

    public Page(GUI gui) {
        this.gui = gui;
        this.studentist = this.gui.getStudentist();
        setLayout(new GridBagLayout());
    }
}
