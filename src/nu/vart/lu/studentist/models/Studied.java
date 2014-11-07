package nu.vart.lu.studentist.models;

public class Studied {
    protected Course course;
    protected String grade;
    protected Student student;

    public Studied(Student student, Course course, String grade) {
        this.student = student;
        this.course = course;
        this.grade = grade;
    }

    public Course getCourse() {
        return course;
    }

    public String getGrade() {
        return grade;
    }

    public Student getStudent() {
        return student;
    }
}