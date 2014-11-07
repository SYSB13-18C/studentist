package nu.vart.lu.studentist;

import nu.vart.lu.studentist.lib.Database;
import nu.vart.lu.studentist.models.Course;
import nu.vart.lu.studentist.models.Student;
import nu.vart.lu.studentist.models.Studied;
import nu.vart.lu.studentist.models.Studies;

public class Studentist {
    public Database database;
    public String title = "Studentist";

    public Studentist(String databaseUri) {
        database = new Database(databaseUri);
    }

    public Course addCourse(String code, String name, int points) throws Course.CodeTooLongException, Course.NameTooLongException, Model.DuplicateKeyException {
        Course course = new Course(code, name, points);
        try {
            if (!database.addCourse(course))
                return null;
        } catch (Model.InvalidValueException e) {
            e.printStackTrace();
            return null;
        }
        return course;
    }

    public Student getStudent(String id) {
        return database.getStudent(id);
    }

    public Student[] getStudents() {
        return database.getStudents();
    }

    public Studied[] getStudiedByCourse(Course course) {
        return database.getStudiedByCourse(course);
    }

    public String toString() {
        return "Studentist : " + title + " - " + database.uri;
    }

    public Studied[] getStudied(Student student) {
        return database.getStudied(student);
    }

    public Studies[] getStudies(Student student) {
        return database.getStudies(student);
    }
}