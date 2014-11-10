package nu.vart.lu.studentist.models;

import nu.vart.lu.studentist.Model;

public class Studies extends Model {
    protected Student student;
    protected Course course;
    protected String semester;

    public Studies(Student student, Course course, String semester) {
        this.student = student;
        this.course = course;
        this.semester = semester;
    }

    public Course getCourse() {
        return course;
    }

    public String getSemester() {
        return semester;
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public void validate() { }

    public class AlreadyStudiesException extends Exception {
        public AlreadyStudiesException() {
            super("The student already studies the course.");
        }
    }

    public class MaxPointsException extends Exception {
        public MaxPointsException(String message) {
            super(message);
        }
    }
}