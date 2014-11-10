import nu.vart.lu.studentist.Studentist;
import nu.vart.lu.studentist.gui.GUI;
import nu.vart.lu.studentist.test.Tests;

import java.sql.*;

public class mainz0r {
    public static void main(String[] argv) {
        // Launch Studentist and a GUI to control it.
        System.out.println("\n # Creating Studentist");
        Studentist studentist = new Studentist("jdbc:sqlserver://vart.nu:8887;databaseName=Studentist;user=mama;password=papa;");

        System.out.println("\n # Creating GUI");
        GUI gui = new GUI(studentist);

        gui.feedback.add("Welcome to Studentist!");
        System.out.println("\n # Loaded " + studentist);

        // Some testing..
        //Tests.testStudentist(studentist);
    }
}