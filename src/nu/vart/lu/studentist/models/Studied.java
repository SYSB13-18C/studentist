package nu.vart.lu.studentist.models;

import nu.vart.lu.studentist.Model;

public class Studied extends Model {
    protected Course course;
    protected String grade;
    protected Student student;
    protected String semester;

    public Studied(Student student, Course course, String grade, String semester) {
        this.student = student;
        this.course = course;
        this.grade = grade;
        this.semester = semester;
    }

    public Course getCourse() {
        return course;
    }

    public String getGrade() {
        return grade;
    }

    public Studied(String semester) {
        this.semester = semester;
    }

    public String getSemester() {
        return semester;
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public void validate() { }
}