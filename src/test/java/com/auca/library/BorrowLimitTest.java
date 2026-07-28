package com.auca.library;

import java.util.UUID;

import org.junit.Test;

import com.auca.library.domain.Book;
import com.auca.library.domain.Location;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.User;
import com.auca.library.exception.BorrowLimitExceededException;

public class BorrowLimitTest extends TestBase {

    @Test
    public void goldMember_withFourActiveBorrows_canBorrowAFifth() {
        Location village = createFullHierarchy("G4" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("g4_" + UUID.randomUUID().toString().substring(0, 5), "pass", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);
        createApprovedMembership(user, gold);

        for (int i = 0; i < 4; i++) {
            Book book = createAvailableBook("GoldBook" + i);
            borrowService.borrowBook(user.getPersonId(), book.getBookId());
        }

        // should not throw - 5th is still allowed
        borrowService.validateBorrowLimit(user.getPersonId());
        Book fifth = createAvailableBook("GoldBook5");
        borrowService.borrowBook(user.getPersonId(), fifth.getBookId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void goldMember_withFiveActiveBorrows_cannotBorrowASixth() {
        Location village = createFullHierarchy("G5" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("g5_" + UUID.randomUUID().toString().substring(0, 5), "pass", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);
        createApprovedMembership(user, gold);

        for (int i = 0; i < 5; i++) {
            Book book = createAvailableBook("GoldMax" + i);
            borrowService.borrowBook(user.getPersonId(), book.getBookId());
        }

        borrowService.validateBorrowLimit(user.getPersonId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void silverMember_withThreeActiveBorrows_isBlocked() {
        Location village = createFullHierarchy("S3" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("s3_" + UUID.randomUUID().toString().substring(0, 5), "pass", village);
        MembershipType silver = createMembershipType("Silver", 3, 30);
        createApprovedMembership(user, silver);

        for (int i = 0; i < 3; i++) {
            Book book = createAvailableBook("SilverBook" + i);
            borrowService.borrowBook(user.getPersonId(), book.getBookId());
        }

        borrowService.validateBorrowLimit(user.getPersonId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void striverMember_withTwoActiveBorrows_isBlocked() {
        Location village = createFullHierarchy("ST" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("st_" + UUID.randomUUID().toString().substring(0, 5), "pass", village);
        MembershipType striver = createMembershipType("Striver", 2, 10);
        createApprovedMembership(user, striver);

        for (int i = 0; i < 2; i++) {
            Book book = createAvailableBook("StriverBook" + i);
            borrowService.borrowBook(user.getPersonId(), book.getBookId());
        }

        borrowService.validateBorrowLimit(user.getPersonId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void userWithoutApprovedMembership_isBlocked() {
        Location village = createFullHierarchy("NM" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("nm_" + UUID.randomUUID().toString().substring(0, 5), "pass", village);

        borrowService.validateBorrowLimit(user.getPersonId());
    }
}
