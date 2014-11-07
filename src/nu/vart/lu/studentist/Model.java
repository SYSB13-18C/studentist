package nu.vart.lu.studentist;

public class Model {
    public class InvalidValueException extends Exception {
        public InvalidValueException(String message) {
            super(message);
        }
    }

    public class DuplicateKeyException extends Exception {
        public DuplicateKeyException(String message) {
            super(message);
        }
    }
}
