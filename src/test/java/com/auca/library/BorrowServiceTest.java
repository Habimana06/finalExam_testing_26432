package com.auca.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import com.auca.library.domain.Book;
import com.auca.library.domain.BookStatus;
import com.auca.library.domain.Borrower;
import com.auca.library.domain.Location;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.User;
import com.auca.library.exception.EntityNotFoundException;
import com.auca.library.exception.InvalidBookStateException;
import com.auca.library.service.BorrowService;

// tests for borrowBook method in BorrowService
public class BorrowServiceTest extends TestBase {

    @Test
    public void borrowBook_availableBook_createsBorrowerRecordWithZeroFine() {
        Location village = createFullHierarchy("BB" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("borrow_" + UUID.randomUUID().toString().substring(0, 5), "pass123", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);
        createApprovedMembership(user, gold);
        Book book = createAvailableBook("Introduction to Software Testing");

        Borrower borrower = borrowService.borrowBook(user.getPersonId(), book.getBookId());

        // fine must be zero on the borrowing date
        assertNotNull(borrower.getId());
        assertEquals(0, borrower.getFine());
        assertEquals(0, borrower.getLateChargeFees());
        assertNotNull(borrower.getPickupDate());
        assertNotNull(borrower.getDueDate());
    }

    @Test
    public void borrowBook_setsBookStatusToBorrowed() {
        Location village = createFullHierarchy("BS" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("status_" + UUID.randomUUID().toString().substring(0, 5), "pass123", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);
        createApprovedMembership(user, gold);
        Book book = createAvailableBook("Database Systems with Hibernate");

        borrowService.borrowBook(user.getPersonId(), book.getBookId());

        Book updated = bookService.findById(book.getBookId());
        assertEquals(BookStatus.BORROWED, updated.getBookStatus());
    }

    @Test
    public void borrowBook_dueDateIsPickupDatePlusLoanPeriod() {
        Location village = createFullHierarchy("BD" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("due_" + UUID.randomUUID().toString().substring(0, 5), "pass123", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);
        createApprovedMembership(user, gold);
        Book book = createAvailableBook("Object Oriented Programming");

        Borrower borrower = borrowService.borrowBook(user.getPersonId(), book.getBookId());

        // due date = pickup date + 14 days
        assertEquals(borrower.getPickupDate().plusDays(BorrowService.LOAN_PERIOD_DAYS), borrower.getDueDate());
    }

    @Test(expected = EntityNotFoundException.class)
    public void borrowBook_unknownReader_throwsEntityNotFoundException() {
        Book book = createAvailableBook("Missing Reader Book");
        borrowService.borrowBook(UUID.randomUUID(), book.getBookId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void borrowBook_unknownBook_throwsEntityNotFoundException() {
        Location village = createFullHierarchy("NB" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("nobook_" + UUID.randomUUID().toString().substring(0, 5), "pass123", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);
        createApprovedMembership(user, gold);
        borrowService.borrowBook(user.getPersonId(), UUID.randomUUID());
    }

    @Test(expected = InvalidBookStateException.class)
    public void borrowBook_unavailableBook_throwsInvalidBookStateException() {
        Location village = createFullHierarchy("UB" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("unavail_" + UUID.randomUUID().toString().substring(0, 5), "pass123", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);
        createApprovedMembership(user, gold);
        Book book = createAvailableBook("Already Borrowed Title");
        borrowService.borrowBook(user.getPersonId(), book.getBookId());
        borrowService.borrowBook(user.getPersonId(), book.getBookId());
    }
}
