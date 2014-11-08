package nu.vart.lu.studentist.models;

import nu.vart.lu.studentist.Model;

public class Studies extends Model {
    protected Student student;
    protected Course course;

    public Studies(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

    public Course getCourse() {
        return course;
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
}