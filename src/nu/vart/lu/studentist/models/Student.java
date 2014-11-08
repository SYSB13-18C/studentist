package nu.vart.lu.studentist.models;

import nu.vart.lu.studentist.Model;

public class Student extends Model {
    protected String id;
    protected String name;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return id + " " + name;
    }

    @Override
    public void validate() { }
}