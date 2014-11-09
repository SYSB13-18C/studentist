package nu.vart.lu.studentist;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import nu.vart.lu.studentist.Model;
import nu.vart.lu.studentist.models.Course;
import nu.vart.lu.studentist.models.Student;
import nu.vart.lu.studentist.models.Studied;
import nu.vart.lu.studentist.models.Studies;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * DAL for our MsSQL database.
 */
public class Database {
    protected String uri;
    public String getUri() { return uri; }

    /**
     * Construct a database layer and connect (mssql)
     *
     * @param uri The URI.
     */
    public Database(String uri) {
        this.uri = uri;
    }

    /**
     * Adds a new course.
     * @param course The course (duh!)
     * @return True if added or false otherwise.
     * @throws Model.InvalidValueException When a value is too long (either code(10) or name(20)).
     * TODO return something better (plenty of errors may occur... id exists.. negative points.. ugly name)
     */
    public boolean addCourse(Course course) throws Model.InvalidValueException, Model.DuplicateKeyException {
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Course VALUES (?, ?, ?)");
            statement.setString(1, course.getCode());
            statement.setString(2, course.getName());
            statement.setInt(3, course.getPoints());
            statement.executeUpdate();
            System.out.println("Course added");
            return true;
        }
        catch (SQLException e) {
            // TODO check documentation for error codes (now we're just assuming)
            if (e.getErrorCode() == 8152)
                throw course.new InvalidValueException("error 8152 " + e.getMessage());
            else if (e.getErrorCode() == 2627)
                throw course.new DuplicateKeyException("error 2627 " + e.getMessage());
            else {
                System.err.println("Unknown addCourse error " + e.getMessage());
                e.printStackTrace(); }
        }
        return false;
    }

    public boolean addStudent(String id, String name) {
        // TODO hantera fel när ID redan finns, con broken, ?
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Student (id, name) VALUES (?, ?)");
            statement.setString(1, id);
            statement.setString(2, name);
            statement.executeQuery();
            System.out.println("Student tillagd");
            return true;
        }
        catch (SQLServerException e) { // ID already exists (perhaps something else too..)
            //System.out.println("ID already exists " + e.getMessage());
            //e.printStackTrace();
        }
        catch (SQLException e) {
            System.err.println("SQL-error when adding student " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean addStudied(Studied studied) {
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Studied (student, course, grade) VALUES (?, ?, ?)");
            statement.setString(1, studied.getStudent().getId());
            statement.setString(2, studied.getCourse().getCode());
            statement.setString(3, studied.getGrade());
            statement.executeUpdate();
            System.out.println("Student tillagd till studied");
            return true;
        }
        catch (SQLException e) {
            System.err.println("Kunde inte hitta namn på student " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean addStudies(Studies studies) throws Studies.AlreadyStudiesException {
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Studies (student, course) VALUES (?, ?)");
            statement.setString(1, studies.getStudent().getId());
            statement.setString(2, studies.getCourse().getCode());
            statement.executeUpdate();
            return true;
        }
        catch (SQLServerException e) {
            if (e.getErrorCode() == 2627)
                throw studies.new AlreadyStudiesException();
            else {
                System.err.println("OOOPS! " + e.getMessage());
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            System.err.println("Kunde inte lägga till " + studies.getStudent() + " till " + studies.getCourse() + "." + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteCourse(String code){
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Course WHERE code=?");
            statement.setString(1, code);
            statement.executeQuery();
            System.out.println("Course deleted");
            return true;
        }
        catch (SQLException e) {
            // System.err.println("Kunde inte hitta namn på student " + e.getMessage());
            // e.printStackTrace();
        }
        return false;
    }

    public boolean deleteStudent(String id){
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Student WHERE id=?");
            statement.setString(1, id);
            statement.executeQuery();
            System.out.println("Student borttagen");
            return true;
        }
        catch (SQLException e) {
            //System.err.println("SQL fel. Kunde inte hitta namn på student " + e.getMessage());
            //e.printStackTrace();
        }
        return false;
    }

    public Course[] getAvailableCourses(Student student) {
        List<Course> buffer = new LinkedList<Course>();
        try {
            Connection connection = DriverManager.getConnection(uri);
            //PreparedStatement statement = connection.prepareStatement("SELECT code, name, points FROM Course WHERE code NOT IN (SELECT course FROM Studies WHERE Studies.student=? OR Studied.student=?)");
            PreparedStatement statement = connection.prepareStatement("SELECT code, name, points FROM Course WHERE code NOT IN (SELECT course FROM Studied WHERE student=?) AND code NOT IN (SELECT course FROM Studies WHERE student=?)");
            statement.setString(1, student.getId());
            statement.setString(2, student.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                buffer.add(new Course(result.getString("code"), result.getString("name"), result.getInt("points")));
            }
        } catch (SQLException e) {
            System.err.println("Oh noes!");
            e.printStackTrace();
        }

        Course[] courses = new Course[buffer.size()];
        return buffer.toArray(courses);
    }

    public Course getCourse(String code) {
        Course course = null;
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT code, name, points FROM Course WHERE code = ?");
            statement.setString(1, code);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                course = new Course(result.getString("code"), result.getString("name"), result.getInt("points"));
            }
        } catch (SQLException e) {
            System.err.println("Database.getCourse() error : " + e.getMessage());
        }
        return course;
    }

    // TODO return array
    public List<Course> getCourses() {
        List<Course> courses = new ArrayList<Course>();
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT code, name, points FROM course");
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                courses.add(new Course(result.getString(1), result.getString(2), result.getInt(3)));
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte hitta namn på student " + e.getMessage());
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Find that one student..
     *
     * @param id of student
     * @return The Student
     * @throws SQLException Some SQL exception.
     */
    public Student getStudent(String id) {
        PreparedStatement statement = null;
        Student student = null;
        try {
            Connection connection = DriverManager.getConnection(uri);
            statement = connection.prepareStatement("SELECT id, name FROM Student WHERE id = ?");
            statement.setString(1, id);
            ResultSet result = statement.executeQuery();

            result.next();

            student = new Student(result.getString(1), result.getString(2));

            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return student;
    }

    public Studied[] getStudiedByCourse(Course course) {
        List<Studied> buffer = new LinkedList<Studied>();
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT student, Student.name AS studentName, course, Course.name AS courseName, points, grade FROM Studied INNER JOIN Student ON Student.id = Studied.student INNER JOIN Course ON Studied.course = Course.code WHERE course = ?");
            statement.setString(1, course.getCode());
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                buffer.add(new Studied(
                    new Student(result.getString(1), result.getString(2)),
                    new Course(result.getString(3), result.getString(4), result.getInt(5)),
                    result.getString(6)));
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte hitta namn på student " + e.getMessage());
            e.printStackTrace();
        }

        Studied[] studied = new Studied[buffer.size()];
        return buffer.toArray(studied);
    }

    public Studied[] getStudiedAtCourse(String courseCode) {
        List<Studied> studentOnCourse = new ArrayList<Studied>();
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT student, course, grade FROM studied WHERE course = ?");
            statement.setString(1, courseCode);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                //studentOnCourse.add(new Studied(new Student(result.getString(1), result.getString(2), result.getString(3)));
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte hitta namn på student " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get the complete Student set.
     *
     * @return The Student set.
     */
    public Student[] getStudents() {
        ArrayList<Student> buffer = new ArrayList<Student>();
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM Student");
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                buffer.add(new Student(result.getString(1), result.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Student[] students = new Student[buffer.size()];
        return buffer.toArray(students);
    }

    /**
     * Get a set of students where query matches id or name.
     *
     * @query The query (searches id and name).
     * @return The Student set.
     */
    public Student[] getStudents(String query) {
        ArrayList<Student> buffer = new ArrayList<Student>();
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM Student WHERE id LIKE ? OR name LIKE ?");
            statement.setString(1, "%" + query + "%");
            statement.setString(2, "%" + query + "%");
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                buffer.add(new Student(result.getString(1), result.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Student[] students = new Student[buffer.size()];
        return buffer.toArray(students);
    }

    /**
     * Get a set of courses where query matches code or name.
     *
     * @query The query (searches code and name).
     * @return The Student set.
     */
    public Course[] getCourses(String query) {
        ArrayList<Course> buffer = new ArrayList<Course>();
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT code, name, points FROM Course WHERE code LIKE ? OR name LIKE ?");
            statement.setString(1, "%" + query + "%");
            statement.setString(2, "%" + query + "%");
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                buffer.add(new Course(result.getString("code"), result.getString("name"), result.getInt("points")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Course[] courses = new Course[buffer.size()];
        return buffer.toArray(courses);
    }

    public Student[] getStudentsByCourse(Course course) {
        return getStudentsByCourse(course.getCode());
    }

    public Student[] getStudentsByCourse(String course) {
        List<Student> buffer = new ArrayList<Student>();
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT student.id, student.name FROM Student LEFT JOIN studies ON student.id=studies.student WHERE course=? ");
            statement.setString(1, course);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                buffer.add(new Student(result.getString(1), result.getString(2)));
            }
        } catch (SQLException e) {
            System.err.println("Kunde inte hitta namn på student " + e.getMessage());
            e.printStackTrace();
        }

        Student[] students = new Student[buffer.size()];
        return buffer.toArray(students);
    }

    public Studied[] getStudied(Student student) {
        List<Studied> buffer = new LinkedList<Studied>();
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT student, course, grade FROM Studied WHERE student=?");
            statement.setString(1, student.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                buffer.add(new Studied(student, getCourse(result.getString(2)), result.getString(3))); }
        } catch (SQLException e) {
            System.err.println("Oh noes!");
            e.printStackTrace();
        }

        Studied[] studied = new Studied[buffer.size()];
        return buffer.toArray(studied);
    }

    public Studies[] getStudies(Student student) {
        List<Studies> buffer = new LinkedList<Studies>();
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT student, course FROM Studies WHERE student=?");
            statement.setString(1, student.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                buffer.add(new Studies(student, getCourse(result.getString(2))));
            }
        } catch (SQLException e) {
            System.err.println("Oh noes!");
            e.printStackTrace();
        }

        Studies[] studies = new Studies[buffer.size()];
        return buffer.toArray(studies);
    }

    public boolean remove(Course course) {
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Course WHERE code=?");
            statement.setString(1, course.getCode());
            if (statement.executeUpdate() > 0)
                return true;
        } catch (SQLException e) {
            System.err.println("Oh noes!");
            e.printStackTrace();
        }
        return false;
    }

    public boolean remove(Studied studied) {
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Studied WHERE student=? and course=?");
            statement.setString(1, studied.getStudent().getId());
            statement.setString(2, studied.getCourse().getCode());
            if (statement.executeUpdate() > 0)
                return true;
        } catch (SQLException e) {
            System.err.println("Oh noes!");
            e.printStackTrace();
        }
        return false;
    }

    public boolean remove(Studies studies) {
        try {
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Studies WHERE student=? AND course=?");
            statement.setString(1, studies.getStudent().getId());
            statement.setString(2, studies.getCourse().getCode());
            if (statement.executeUpdate() > 0)
                return true;
        } catch (SQLException e) {
            System.err.println("Oh noes!");
            e.printStackTrace();
        }
        return false;
    }
}