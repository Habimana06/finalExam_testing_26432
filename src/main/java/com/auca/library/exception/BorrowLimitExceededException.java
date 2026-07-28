package com.auca.library.exception;

// thrown when user wants to borrow more books than membership allows
public class BorrowLimitExceededException extends RuntimeException {

    public BorrowLimitExceededException(String message) {
        super(message);
    }
}
