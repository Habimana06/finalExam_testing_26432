package com.auca.library;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.auca.library.domain.Book;
import com.auca.library.domain.BookStatus;
import com.auca.library.domain.Gender;
import com.auca.library.domain.Location;
import com.auca.library.domain.LocationType;
import com.auca.library.domain.Membership;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.Role;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;
import com.auca.library.domain.User;
import com.auca.library.service.BookService;
import com.auca.library.service.BorrowService;
import com.auca.library.service.LocationService;
import com.auca.library.service.MembershipService;
import com.auca.library.service.RoomService;
import com.auca.library.service.UserService;
import com.auca.library.util.HibernateUtil;

// shared setup for tests
public class TestBase {

    protected static SessionFactory sessionFactory;
    protected static LocationService locationService;
    protected static UserService userService;
    protected static MembershipService membershipService;
    protected static BorrowService borrowService;
    protected static BookService bookService;
    protected static RoomService roomService;

    @BeforeClass
    public static void setUpClass() {
        // use the same application.properties as the main app
        sessionFactory = HibernateUtil.buildSessionFactory("application.properties");
        locationService = new LocationService(sessionFactory);
        userService = new UserService(sessionFactory);
        membershipService = new MembershipService(sessionFactory);
        borrowService = new BorrowService(sessionFactory);
        bookService = new BookService(sessionFactory);
        roomService = new RoomService(sessionFactory);
    }

    @AfterClass
    public static void tearDownClass() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    // helper to create full location hierarchy and return village
    protected Location createFullHierarchy(String prefix) {
        Location province = new Location(UUID.randomUUID(), prefix + "-PROV", prefix + " Province",
                LocationType.PROVINCE, null);
        province = locationService.createLocation(province, null);

        Location district = new Location(UUID.randomUUID(), prefix + "-DIST", prefix + " District",
                LocationType.DISTRICT, null);
        district = locationService.createLocation(district, province.getLocationId());

        Location sector = new Location(UUID.randomUUID(), prefix + "-SEC", prefix + " Sector",
                LocationType.SECTOR, null);
        sector = locationService.createLocation(sector, district.getLocationId());

        Location cell = new Location(UUID.randomUUID(), prefix + "-CELL", prefix + " Cell",
                LocationType.CELL, null);
        cell = locationService.createLocation(cell, sector.getLocationId());

        Location village = new Location(UUID.randomUUID(), prefix + "-VIL", prefix + " Village",
                LocationType.VILLAGE, null);
        village = locationService.createLocation(village, cell.getLocationId());

        return village;
    }

    // helper to create a user in a village
    protected User createUser(String username, String password, Location village) {
        User user = new User();
        user.setPersonId(UUID.randomUUID());
        user.setFirstName("Test");
        user.setLastName("User");
        user.setGender(Gender.MALE);
        user.setPhoneNumber("0780000000");
        user.setPassword(password);
        user.setRole(Role.STUDENT);
        user.setUserName(username);
        user.setVillage(village);
        return userService.registerUser(user);
    }

    // helper to create membership type
    protected MembershipType createMembershipType(String name, int maxBooks, int price) {
        MembershipType type = new MembershipType(UUID.randomUUID(), name, maxBooks, price);
        return membershipService.saveMembershipType(type);
    }

    // helper to create approved membership
    protected Membership createApprovedMembership(User user, MembershipType type) {
        Membership membership = membershipService.registerMembership(user.getPersonId(), type.getMembershipTypeId());
        return membershipService.approveMembership(membership.getMembershipId());
    }

    // helper to create room, shelf and available book
    protected Book createAvailableBook(String title) {
        Room room = roomService.saveRoom(new Room(UUID.randomUUID(), "R-" + UUID.randomUUID().toString().substring(0, 4)));
        Shelf shelf = roomService.saveShelf(new Shelf(UUID.randomUUID(), "General", 0, 0, 0, room));
        Book book = new Book(UUID.randomUUID(), title, "ISBN-" + UUID.randomUUID().toString().substring(0, 6),
                "AUCA Press", LocalDate.of(2020, 1, 1), 1, BookStatus.AVAILABLE, shelf);
        return bookService.saveBook(book);
    }
}
