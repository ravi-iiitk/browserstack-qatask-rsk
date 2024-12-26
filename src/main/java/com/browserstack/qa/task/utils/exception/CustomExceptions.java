package com.browserstack.qa.task.utils.exception;


public class CustomExceptions {

    public static class ElementNotFoundException extends RuntimeException {
        public ElementNotFoundException(String message) {
            super(message);
        }
    }

    public static class ElementNotClickableException extends RuntimeException {
        public ElementNotClickableException(String message) {
            super(message);
        }
    }

    public static class ElementNotVisibleException extends RuntimeException {
        public ElementNotVisibleException(String message) {
            super(message);
        }
    }

    public static class InvalidTestDataException extends RuntimeException {
        public InvalidTestDataException(String message) {
            super(message);
        }
    }

    public static class DriverInitializationException extends RuntimeException {
        public DriverInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
