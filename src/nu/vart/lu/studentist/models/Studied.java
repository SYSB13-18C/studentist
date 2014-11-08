package nu.vart.lu.studentist.models;

import nu.vart.lu.studentist.Model;

public class Studied extends Model {
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

    @Override
    public void validate() { }
}