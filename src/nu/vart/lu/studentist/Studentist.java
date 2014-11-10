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

    public boolean add(Student student) throws Model.DuplicateKeyException {
        return database.add(student);
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

    public Studied completeCourse(Studies studies, String grade) {
        Studied studied = new Studied(studies.getStudent(), studies.getCourse(), grade);
        if (!database.addStudied(studied))
            return null; // TODO throw some error

        if (!database.remove(studies))
            return null; // TODO throw some error
        return studied;
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
        return "Studentist : " + title + " - " + database.getUri();
    }

    public Studied[] getStudied(Student student) {
        return database.getStudied(student);
    }

    public Studies[] getStudies(Student student) {
        return database.getStudies(student);
    }

    public boolean remove(Course course) { return database.remove(course); }

    public boolean remove(Studied studied) { return database.remove(studied); }

    public boolean remove(Studies studies) { return database.remove(studies); }

    public Course[] getAvailableCourses(Student student) { return database.getAvailableCourses(student); }

    public Studies assign(Student student, Course course) throws Studies.AlreadyStudiesException {
        Studies studies = new Studies(student, course);
        studies.validate();
        database.addStudies(studies);
        return studies;
    }
}