package nu.vart.lu.studentist.models;

import nu.vart.lu.studentist.Model;

public class Course extends Model {
    protected String code;
    protected String name;
    protected int points;

    public Course(String code, String name, int points) {
        this.code = code;
        this.name = name;
        this.points = points;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return code;
    }

    /**
     * TODO
     *  handle empty (0 length) code and string values.
     *  restrictions on points (negative? more than 30?)
     */
    @Override
    public void validate() throws CodeTooLongException, NameTooLongException {
        if (code.length() > 10)
            throw new CodeTooLongException();
        if (name.length() > 20)
            throw new NameTooLongException();
    }

    public class CodeTooLongException extends Exception {
        public CodeTooLongException() {
            super("Course code value is too long (max 10 characters).");
        }
    }

    public class NameTooLongException extends Exception {
        public NameTooLongException() {
            super("Course name value is too long (max 20 characters).");
        }
    }
}