package nu.vart.lu.studentist;

import nu.vart.lu.studentist.models.Course;

public abstract class Model {
    // NOTE: not optimal.. type of extended exception seems lost
    public abstract void validate() throws Exception;

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

    public class HasRelationsException extends Exception {
        public HasRelationsException(String message) {
            super(message);
        }
    }
}