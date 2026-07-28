package com.auca.library;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import com.auca.library.domain.Book;
import com.auca.library.domain.Borrower;
import com.auca.library.domain.Location;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.User;

public class LateFeeTest extends TestBase {

    @Test
    public void returnedOnDueDate_feeIsZero() {
        Location village = createFullHierarchy("LF0" + UUID.randomUUID().toString().substring(0, 3));
        User user = createUser("lf0_" + UUID.randomUUID().toString().substring(0, 5), "pass", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);
        createApprovedMembership(user, gold);
        Book book = createAvailableBook("FeeZero");

        Borrower borrower = borrowService.borrowBook(user.getPersonId(), book.getBookId());
        setBorrowerDates(borrower.getId(), LocalDate.now().minusDays(14), LocalDate.now(), LocalDate.now());

        int fee = borrowService.calculateLateFee(borrower.getId());
        assertEquals(0, fee);
    }

    @Test
    public void goldMember_returnedThreeDaysLate_feeIs150() {
        Location village = createFullHierarchy("LFG" + UUID.randomUUID().toString().substring(0, 3));
        User user = createUser("lfg_" + UUID.randomUUID().toString().substring(0, 5), "pass", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);
        createApprovedMembership(user, gold);
        Book book = createAvailableBook("FeeGold");

        Borrower borrower = borrowService.borrowBook(user.getPersonId(), book.getBookId());
        LocalDate due = LocalDate.now().minusDays(3);
        setBorrowerDates(borrower.getId(), due.minusDays(14), due, LocalDate.now());

        int fee = borrowService.calculateLateFee(borrower.getId());
        assertEquals(150, fee);
    }

    @Test
    public void silverMember_returnedFiveDaysLate_feeIs150() {
        Location village = createFullHierarchy("LFS" + UUID.randomUUID().toString().substring(0, 3));
        User user = createUser("lfs_" + UUID.randomUUID().toString().substring(0, 5), "pass", village);
        MembershipType silver = createMembershipType("Silver", 3, 30);
        createApprovedMembership(user, silver);
        Book book = createAvailableBook("FeeSilver");

        Borrower borrower = borrowService.borrowBook(user.getPersonId(), book.getBookId());
        LocalDate due = LocalDate.now().minusDays(5);
        setBorrowerDates(borrower.getId(), due.minusDays(14), due, LocalDate.now());

        int fee = borrowService.calculateLateFee(borrower.getId());
        assertEquals(150, fee);
    }

    @Test
    public void striverMember_returnedOneDayLate_feeIs10() {
        Location village = createFullHierarchy("LFT" + UUID.randomUUID().toString().substring(0, 3));
        User user = createUser("lft_" + UUID.randomUUID().toString().substring(0, 5), "pass", village);
        MembershipType striver = createMembershipType("Striver", 2, 10);
        createApprovedMembership(user, striver);
        Book book = createAvailableBook("FeeStriver");

        Borrower borrower = borrowService.borrowBook(user.getPersonId(), book.getBookId());
        LocalDate due = LocalDate.now().minusDays(1);
        setBorrowerDates(borrower.getId(), due.minusDays(14), due, LocalDate.now());

        int fee = borrowService.calculateLateFee(borrower.getId());
        assertEquals(10, fee);
    }

    @Test
    public void notYetReturned_feeIsComputedAgainstToday() {
        Location village = createFullHierarchy("LFN" + UUID.randomUUID().toString().substring(0, 3));
        User user = createUser("lfn_" + UUID.randomUUID().toString().substring(0, 5), "pass", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);
        createApprovedMembership(user, gold);
        Book book = createAvailableBook("FeeOpen");

        Borrower borrower = borrowService.borrowBook(user.getPersonId(), book.getBookId());
        LocalDate due = LocalDate.now().minusDays(2);
        setBorrowerDates(borrower.getId(), due.minusDays(14), due, null);

        int fee = borrowService.calculateLateFee(borrower.getId());
        assertEquals(100, fee); // 2 days late * 50 Rwf
    }

    // helper to change pickup, due and return dates for fee tests
    private void setBorrowerDates(UUID borrowerId, LocalDate pickup, LocalDate due, LocalDate returned) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Borrower managed = session.get(Borrower.class, borrowerId);
            managed.setPickupDate(pickup);
            managed.setDueDate(due);
            managed.setReturnDate(returned);
            session.merge(managed);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }
}
