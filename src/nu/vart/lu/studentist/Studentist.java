package nu.vart.lu.studentist;

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

    public void add(Student student) throws Model.DuplicateKeyException {
        database.add(student);
    }

    public void add(Course course) throws Model.DuplicateKeyException, Model.InvalidValueException {
        database.add(course);
    }

    public Studies assign(Student student, Course course, String semester) throws Studies.AlreadyStudiesException, Studies.MaxPointsException {
        Studies studies = new Studies(student, course, semester);
        studies.validate();
        database.addStudies(studies);
        return studies;
    }

    public Studied completeCourse(Studies studies, String grade) {
        Studied studied = new Studied(studies.getStudent(), studies.getCourse(), grade, studies.getSemester());
        database.addStudied(studied);
        database.remove(studies);
        return studied;
    }

    public Course[] getAvailableCourses(Student student) { return database.getAvailableCourses(student); }

    public Student[] getAvailableStudents(Course course) { return database.getAvailableStudents(course); }

    public Student getStudent(String id) {
        return database.getStudent(id);
    }

    public Student[] getStudents() {
        return database.getStudents();
    }

    public Studied[] getStudiedByCourse(Course course) {
        return database.getStudiedByCourse(course);
    }

    public Studied[] getStudied(Student student) {
        return database.getStudied(student);
    }

    public Studies[] getStudies(Student student) {
        return database.getStudies(student);
    }

    public Studied[] getStudied(Course course) {
        return database.getStudied(course);
    }

    public Studies[] getStudies(Course course) {
        return database.getStudies(course);
    }

    public void remove(Course course) throws Model.HasRelationsException {
        database.remove(course); }

    public void remove(Student student) throws Model.HasRelationsException {
        database.remove(student); }

    public void remove(Studied studied) { database.remove(studied); }

    public void remove(Studies studies) { database.remove(studies); }

    public String toString() {
        return "Studentist : " + title + " - " + database.getUri();
    }
}