package nu.vart.lu.studentist;

import com.microsoft.sqlserver.jdbc.SQLServerException;
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
    public void add(Course course) throws Model.InvalidValueException, Model.DuplicateKeyException {
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Course VALUES (?, ?, ?)");
            statement.setString(1, course.getCode());
            statement.setString(2, course.getName());
            statement.setInt(3, course.getPoints());
            statement.executeUpdate();
            System.out.println("timer : " + timer.stop() + " milliseconds");
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
    }

    public boolean add(Student student) throws Model.DuplicateKeyException {
        // TODO hantera fel när ID redan finns, con broken, ?
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Student (id, name) VALUES (?, ?)");
            statement.setString(1, student.getId());
            statement.setString(2, student.getName());
            statement.executeUpdate();
            System.out.println("timer : " + timer.stop() + " milliseconds");
            return true;
        }
        catch (SQLServerException e) { // ID already exists (perhaps something else too..)
            throw student.new DuplicateKeyException("Oh noes!");
        }
        catch (SQLException e) {
            System.err.println("SQL-error when adding student " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean add(Studied studied) {
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Studied (student, course, grade, semester) VALUES (?, ?, ?, ?)");
            statement.setString(1, studied.getStudent().getId());
            statement.setString(2, studied.getCourse().getCode());
            statement.setString(3, studied.getGrade());
            statement.setString(4, studied.getSemester());
            statement.executeUpdate();
            System.out.println("timer : " + timer.stop() + " milliseconds");
            return true;
        }
        catch (SQLException e) {
            System.err.println("Kunde inte hitta namn på student " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean add(Studies studies) throws Studies.AlreadyStudiesException, Studies.MaxPointsException {
        if (getPoints(studies.getStudent(), studies.getSemester()) + studies.getCourse().getPoints() > 45)
            throw studies.new MaxPointsException("Oh no!");

        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Studies (student, course, semester) VALUES (?, ?, ?)");
            statement.setString(1, studies.getStudent().getId());
            statement.setString(2, studies.getCourse().getCode());
            statement.setString(3, studies.getSemester());
            statement.executeUpdate();
            System.out.println("timer : " + timer.stop() + " milliseconds");
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

    public Course[] getAvailableCourses(Student student) {
        List<Course> buffer = new LinkedList<Course>();
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            //PreparedStatement statement = connection.prepareStatement("SELECT code, name, points FROM Course WHERE code NOT IN (SELECT course FROM Studies WHERE Studies.student=? OR Studied.student=?)");
            PreparedStatement statement = connection.prepareStatement("SELECT code, name, points FROM Course WHERE code NOT IN (SELECT course FROM Studied WHERE student=?) AND code NOT IN (SELECT course FROM Studies WHERE student=?)");
            statement.setString(1, student.getId());
            statement.setString(2, student.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                buffer.add(new Course(result.getString("code"), result.getString("name"), result.getInt("points")));
            }
            System.out.println("timer : " + timer.stop() + " milliseconds");
        } catch (SQLException e) {
            System.err.println("Oh noes!");
            e.printStackTrace();
        }

        Course[] courses = new Course[buffer.size()];
        return buffer.toArray(courses);
    }

    public Student[] getAvailableStudents(Course course) {
        List<Student> buffer = new LinkedList<Student>();
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM Student WHERE id NOT IN (SELECT student FROM Studied WHERE course=?) AND id NOT IN (SELECT student FROM Studies WHERE course=?)");
            statement.setString(1, course.getCode());
            statement.setString(2, course.getCode());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                buffer.add(new Student(result.getString("id"), result.getString("name")));
            }
            System.out.println("timer : " + timer.stop() + " milliseconds");
        } catch (SQLException e) {
            System.err.println("Oh noes!");
            e.printStackTrace();
        }

        Student[] students = new Student[buffer.size()];
        return buffer.toArray(students);
    }

    public Course getCourse(String code) {
        Course course = null;
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT code, name, points FROM Course WHERE code = ?");
            statement.setString(1, code);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                course = new Course(result.getString("code"), result.getString("name"), result.getInt("points"));
            }
            System.out.println("timer : " + timer.stop() + " milliseconds");
        } catch (SQLException e) {
            System.err.println("Database.getCourse() error : " + e.getMessage());
        }
        return course;
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
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT code, name, points FROM Course WHERE code LIKE ? OR name LIKE ?");
            statement.setString(1, "%" + query + "%");
            statement.setString(2, "%" + query + "%");
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                buffer.add(new Course(result.getString("code"), result.getString("name"), result.getInt("points")));
            }
            System.out.println("timer : " + timer.stop() + " milliseconds");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Course[] courses = new Course[buffer.size()];
        return buffer.toArray(courses);
    }


    public int getPoints(Student student, String semester) {
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT SUM(points) AS points FROM Studies INNER JOIN Course ON Studies.course = Course.code WHERE Studies.student = ? AND Studies.semester = ?");
            statement.setString(1, student.getId());
            statement.setString(2, semester);
            ResultSet result = statement.executeQuery();
            result.next();
            System.out.println("timer : " + timer.stop() + " milliseconds");
            return result.getInt("points");
        } catch (SQLException e) {
            System.err.println("Uh oh! " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Find that one student..
     *
     * @param id of student
     * @return The Student
     */
    public Student getStudent(String id) {
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM Student WHERE id = ?");
            statement.setString(1, id);
            ResultSet result = statement.executeQuery();

            result.next();

            Student student = new Student(result.getString("id"), result.getString("name"));

            result.close();
            System.out.println("timer : " + timer.stop() + " milliseconds");
            return student;
        }
        catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the complete Student set.
     *
     * @return The Student set.
     */
    public Student[] getStudents() {
        ArrayList<Student> buffer = new ArrayList<Student>();
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM Student");
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                buffer.add(new Student(result.getString("id"), result.getString("name")));
            }
            System.out.println("timer : " + timer.stop() + " milliseconds");
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
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM Student WHERE id LIKE ? OR name LIKE ?");
            statement.setString(1, "%" + query + "%");
            statement.setString(2, "%" + query + "%");
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                buffer.add(new Student(result.getString("id"), result.getString("name")));
            }
            System.out.println("timer : " + timer.stop() + " milliseconds");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Student[] students = new Student[buffer.size()];
        return buffer.toArray(students);
    }

    public Student[] getStudents(Course course) {
        List<Student> buffer = new ArrayList<Student>();
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT student.id, student.name FROM Student LEFT JOIN studies ON student.id=studies.student WHERE course=? ");
            statement.setString(1, course.getCode());
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                buffer.add(new Student(result.getString("student.id"), result.getString("student.name")));
            }
            System.out.println("timer : " + timer.stop() + " milliseconds");
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
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT student, course, grade, semester FROM Studied WHERE student=?");
            statement.setString(1, student.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                buffer.add(new Studied(student, getCourse(result.getString("course")), result.getString("grade"), result.getString("semester"))); }
            System.out.println("timer : " + timer.stop() + " milliseconds");
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
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT student, course, semester FROM Studies WHERE student=?");
            statement.setString(1, student.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                buffer.add(new Studies(student, getCourse(result.getString("course")), result.getString("semester")));
            }
            System.out.println("timer : " + timer.stop() + " milliseconds");
        } catch (SQLException e) {
            System.err.println("Oh noes!");
            e.printStackTrace();
        }

        Studies[] studies = new Studies[buffer.size()];
        return buffer.toArray(studies);
    }

    public Studied[] getStudied(Course course) {
        List<Studied> buffer = new LinkedList<Studied>();
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT student, grade, semester FROM Studied WHERE course=?");
            statement.setString(1, course.getCode());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                buffer.add(new Studied(getStudent(result.getString("student")), course, result.getString("grade"), result.getString("semester"))); }
            System.out.println("timer : " + timer.stop() + " milliseconds");
        } catch (SQLException e) {
            System.err.println("Oh noes!");
            e.printStackTrace();
        }

        Studied[] studied = new Studied[buffer.size()];
        return buffer.toArray(studied);
    }

    public Studies[] getStudies(Course course) {
        List<Studies> buffer = new LinkedList<Studies>();
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("SELECT student, course, semester FROM Studies WHERE course=?");
            statement.setString(1, course.getCode());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                buffer.add(new Studies(getStudent(result.getString("student")), course, result.getString("semester")));
            }
            System.out.println("timer : " + timer.stop() + " milliseconds");
        } catch (SQLException e) {
            System.err.println("Oh noes!");
            e.printStackTrace();
        }

        Studies[] studies = new Studies[buffer.size()];
        return buffer.toArray(studies);
    }

    public void remove(Course course) throws Model.HasRelationsException {
        // TODO catch SQLServerException -- The DELETE statement conflicted with the REFERENCE constraint "STUDIES_FK_COURSE".
        //   or (first) remove all Studies and Studied relation
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Course WHERE code=?");
            statement.setString(1, course.getCode());
            statement.executeUpdate();
            System.out.println("timer : " + timer.stop() + " milliseconds");
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw course.new HasRelationsException(e.getMessage());
            }
            System.err.println("Oh noes! " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void remove(Student student) throws Model.HasRelationsException {
        // TODO remove relations (Studies, Studied)
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Student WHERE id=?");
            statement.setString(1, student.getId());
            statement.executeUpdate();
            System.out.println("timer : " + timer.stop() + " milliseconds");
        }
        catch (SQLException e) {
            // TODO more reliable check (getErrorCode gives vendor code, not good)
            if (e.getErrorCode() == 547 || e.getErrorCode() == 1451) {
                throw student.new HasRelationsException(e.getMessage());
            }
            else {
                System.err.println("Oh noes! " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void remove(Studied studied) {
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Studied WHERE student=? and course=?");
            statement.setString(1, studied.getStudent().getId());
            statement.setString(2, studied.getCourse().getCode());
            statement.executeUpdate();
            System.out.println("timer : " + timer.stop() + " milliseconds");
        } catch (SQLException e) {
            System.err.println("Oh noes! " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void remove(Studies studies) {
        try {
            Timer timer = new Timer();
            Connection connection = DriverManager.getConnection(uri);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Studies WHERE student=? AND course=?");
            statement.setString(1, studies.getStudent().getId());
            statement.setString(2, studies.getCourse().getCode());
            statement.executeUpdate();
            System.out.println("timer : " + timer.stop() + " milliseconds");
        } catch (SQLException e) {
            System.err.println("Oh noes! " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected class Timer {
        protected long start;
        protected long stop;
        protected long time;

        public Timer() {
            start = System.currentTimeMillis();
        }

        public long stop() {
            stop = System.currentTimeMillis();
            time = stop - start;
            return time;
        }
    }
}