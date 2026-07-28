package com.auca.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.hibernate.SessionFactory;

import com.auca.library.dao.BookDao;
import com.auca.library.dao.BorrowerDao;
import com.auca.library.dao.MembershipDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.dao.UserDao;
import com.auca.library.domain.Book;
import com.auca.library.domain.BookStatus;
import com.auca.library.domain.Borrower;
import com.auca.library.domain.Membership;
import com.auca.library.domain.Shelf;
import com.auca.library.domain.User;
import com.auca.library.exception.BorrowLimitExceededException;

public class BorrowService {

    // loan period is 14 days
    public static final int LOAN_PERIOD_DAYS = 14;

    private BorrowerDao borrowerDao;
    private BookDao bookDao;
    private UserDao userDao;
    private MembershipDao membershipDao;
    private ShelfDao shelfDao;

    public BorrowService(SessionFactory sessionFactory) {
        this.borrowerDao = new BorrowerDao(sessionFactory);
        this.bookDao = new BookDao(sessionFactory);
        this.userDao = new UserDao(sessionFactory);
        this.membershipDao = new MembershipDao(sessionFactory);
        this.shelfDao = new ShelfDao(sessionFactory);
    }

    // check if user can still borrow based on membership max books
    public void validateBorrowLimit(UUID readerId) {
        Membership membership = membershipDao.findApprovedByUserId(readerId);
        if (membership == null) {
            throw new BorrowLimitExceededException("User does not have an approved membership");
        }

        int maxBooks = membership.getMembershipType().getMaxBooks();
        int activeBorrows = borrowerDao.countActiveBorrows(readerId);

        if (activeBorrows >= maxBooks) {
            throw new BorrowLimitExceededException(
                    "Borrow limit exceeded. Max allowed is " + maxBooks);
        }
    }

    // borrow a book
    public Borrower borrowBook(UUID readerId, UUID bookId) {
        User reader = userDao.findById(readerId);
        if (reader == null) {
            throw new IllegalArgumentException("Reader not found");
        }

        Book book = bookDao.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }
        if (book.getBookStatus() != BookStatus.AVAILABLE) {
            throw new IllegalArgumentException("Book is not available");
        }

        // check membership borrow limit
        validateBorrowLimit(readerId);

        LocalDate pickupDate = LocalDate.now();
        LocalDate dueDate = pickupDate.plusDays(LOAN_PERIOD_DAYS);

        Borrower borrower = new Borrower();
        borrower.setId(UUID.randomUUID());
        borrower.setBook(book);
        borrower.setReader(reader);
        borrower.setPickupDate(pickupDate);
        borrower.setDueDate(dueDate);
        borrower.setReturnDate(null);
        borrower.setFine(0); // fine starts at zero
        borrower.setLateChargeFees(0);

        book.setBookStatus(BookStatus.BORROWED);
        bookDao.update(book);

        if (book.getShelf() != null) {
            Shelf shelf = shelfDao.findById(book.getShelf().getShelfId());
            if (shelf != null && shelf.getAvailableStock() > 0) {
                shelf.setAvailableStock(shelf.getAvailableStock() - 1);
                shelf.setBorrowedNumber(shelf.getBorrowedNumber() + 1);
                shelfDao.update(shelf);
            }
        }

        return borrowerDao.save(borrower);
    }

    // calculate late fee = days late * membership daily rate
    public int calculateLateFee(UUID borrowerId) {
        Borrower borrower = borrowerDao.findById(borrowerId);
        if (borrower == null) {
            throw new IllegalArgumentException("Borrower record not found");
        }

        Membership membership = membershipDao.findApprovedByUserId(borrower.getReader().getPersonId());
        if (membership == null) {
            // try any membership that was used - still need daily rate
            throw new IllegalArgumentException("No approved membership found for fee calculation");
        }

        int dailyRate = membership.getMembershipType().getPrice();

        LocalDate endDate = borrower.getReturnDate();
        if (endDate == null) {
            endDate = LocalDate.now(); // not yet returned, use today
        }

        long daysLate = ChronoUnit.DAYS.between(borrower.getDueDate(), endDate);
        if (daysLate < 0) {
            daysLate = 0;
        }

        int fee = (int) daysLate * dailyRate;
        borrower.setLateChargeFees(fee);
        borrower.setFine(fee);
        borrowerDao.update(borrower);
        return fee;
    }

    // helper to mark book as returned (for tests)
    public Borrower returnBook(UUID borrowerId, LocalDate returnDate) {
        Borrower borrower = borrowerDao.findById(borrowerId);
        if (borrower == null) {
            throw new IllegalArgumentException("Borrower record not found");
        }
        borrower.setReturnDate(returnDate);
        borrowerDao.update(borrower);

        Book book = bookDao.findById(borrower.getBook().getBookId());
        if (book != null) {
            book.setBookStatus(BookStatus.AVAILABLE);
            bookDao.update(book);
        }

        calculateLateFee(borrowerId);
        return borrowerDao.findById(borrowerId);
    }
}
