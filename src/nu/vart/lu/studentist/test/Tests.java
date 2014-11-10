package nu.vart.lu.studentist.test;

import nu.vart.lu.studentist.Model;
import nu.vart.lu.studentist.Studentist;
import nu.vart.lu.studentist.Database;
import nu.vart.lu.studentist.models.Course;

public class Tests {
    public static void testStudentist(Studentist studentist) {
        System.out.println("\n\n### TESTING ###");

        System.out.println("Student with id 'ID1' : " + studentist.getStudent("ID1"));

        System.out.println("All students (length): " + studentist.getStudents().length);

        /* STUDENT */

        /* COURSE */

        // Course with too long name
        try {
            Course course = new Course("DUMMY", "i am not as dumb as you think i am", 666);
            course.validate();
            studentist.add(course);
            System.out.println("Added Course: " + course);
        } catch (Course.CodeTooLongException e) {
            System.err.println(e.getMessage());
        } catch (Course.NameTooLongException e) {
            System.err.println(e.getMessage());
        } catch (Model.DuplicateKeyException e) {
            System.err.println(e.getMessage());
        } catch (Model.InvalidValueException e) {
            System.err.println(e.getMessage());
        }

        // Course with too long code
        try {
            Course course = new Course("DUMMYCOURSE", "i am not so dumb", 666);
            course.validate();
            studentist.add(course);
            System.out.println("Added Course: " + course);
        } catch (Course.CodeTooLongException e) {
            System.err.println(e.getMessage());
        } catch (Course.NameTooLongException e) {
            System.err.println(e.getMessage());
        } catch (Model.DuplicateKeyException e) {
            System.err.println(e.getMessage());
        } catch (Model.InvalidValueException e) {
            System.err.println(e.getMessage());
        }

        // Valid course (unless key exists, so it's probably not actually valid)
        try {
            Course course = new Course("DUMMY", "i am not so dumb", 666);
            course.validate();
            studentist.add(course);
            System.out.println("Added Course: " + course);
        } catch (Course.CodeTooLongException e) {
            System.err.println(e.getMessage());
        } catch (Course.NameTooLongException e) {
            System.err.println(e.getMessage());
        } catch (Model.DuplicateKeyException e) {
            System.err.println(e.getMessage());
        } catch (Model.InvalidValueException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("All finished 'CODE1' students : " + studentist.getStudiedByCourse(studentist.database.getCourse("CODE1")));
    }
}