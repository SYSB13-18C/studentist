import nu.vart.lu.studentist.Studentist;
import nu.vart.lu.studentist.gui.GUI;
import nu.vart.lu.studentist.test.Tests;

import java.sql.*;

public class mainz0r {
    public static void main(String[] argv) {
        // Launch Studentist and a GUI to control it.
        System.out.println("Creating Studentist");
        Studentist studentist = new Studentist("jdbc:sqlserver://vart.nu:8888;databaseName=Studentist;user=mama;password=papa;");

        System.out.println("Creating GUI");
        new GUI(studentist);
        System.out.println(studentist);

        // Some testing..
        //Tests.testStudentist(studentist);
    }
}