package nu.vart.lu.studentist.models;

public class Studies {
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
}