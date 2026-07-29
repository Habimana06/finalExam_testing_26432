package com.auca.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.Test;

import com.auca.library.domain.Book;
import com.auca.library.domain.BookStatus;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;

// tests for RoomService only
public class RoomServiceTest extends TestBase {

    @Test
    public void assignShelfToRoom_updatesShelfRoomId() {
        Room room1 = roomService.saveRoom(new Room(UUID.randomUUID(), "ROOM-1"));
        Room room2 = roomService.saveRoom(new Room(UUID.randomUUID(), "ROOM-2"));
        Shelf shelf = roomService.saveShelf(new Shelf(UUID.randomUUID(), "History", 0, 0, 0, room1));

        roomService.assignShelfToRoom(shelf.getShelfId(), room2.getRoomId());

        Shelf updated = roomService.findShelfById(shelf.getShelfId());
        assertEquals(room2.getRoomId(), updated.getRoom().getRoomId());
    }

    @Test
    public void roomWithMultipleShelves_sumsBookCountsAcrossShelves() {
        Room room = roomService.saveRoom(new Room(UUID.randomUUID(), "ROOM-MULTI"));
        Shelf shelf1 = roomService.saveShelf(new Shelf(UUID.randomUUID(), "Cat1", 0, 0, 0, room));
        Shelf shelf2 = roomService.saveShelf(new Shelf(UUID.randomUUID(), "Cat2", 0, 0, 0, room));

        bookService.saveBook(new Book(UUID.randomUUID(), "Book1", "ISBN1", "P", LocalDate.of(2020, 1, 1), 1,
                BookStatus.AVAILABLE, shelf1));
        bookService.saveBook(new Book(UUID.randomUUID(), "Book2", "ISBN2", "P", LocalDate.of(2020, 1, 1), 1,
                BookStatus.AVAILABLE, shelf1));
        bookService.saveBook(new Book(UUID.randomUUID(), "Book3", "ISBN3", "P", LocalDate.of(2020, 1, 1), 1,
                BookStatus.AVAILABLE, shelf2));

        assertEquals(3, roomService.countBooksInRoom(room.getRoomId()));
    }

    @Test
    public void roomWithNoShelves_returnsZero() {
        Room room = roomService.saveRoom(new Room(UUID.randomUUID(), "ROOM-EMPTY"));
        assertEquals(0, roomService.countBooksInRoom(room.getRoomId()));
    }

    @Test
    public void multipleRooms_returnsRoomWithLowestBookCount() {
        Room roomMany = roomService.saveRoom(new Room(UUID.randomUUID(), "ROOM-MANY"));
        Room roomFew = roomService.saveRoom(new Room(UUID.randomUUID(), "ROOM-FEW"));

        Shelf shelfMany = roomService.saveShelf(new Shelf(UUID.randomUUID(), "Many", 0, 0, 0, roomMany));
        Shelf shelfFew = roomService.saveShelf(new Shelf(UUID.randomUUID(), "Few", 0, 0, 0, roomFew));

        bookService.saveBook(new Book(UUID.randomUUID(), "M1", "IM1", "P", LocalDate.of(2021, 1, 1), 1,
                BookStatus.AVAILABLE, shelfMany));
        bookService.saveBook(new Book(UUID.randomUUID(), "M2", "IM2", "P", LocalDate.of(2021, 1, 1), 1,
                BookStatus.AVAILABLE, shelfMany));
        bookService.saveBook(new Book(UUID.randomUUID(), "M3", "IM3", "P", LocalDate.of(2021, 1, 1), 1,
                BookStatus.AVAILABLE, shelfMany));
        bookService.saveBook(new Book(UUID.randomUUID(), "F1", "IF1", "P", LocalDate.of(2021, 1, 1), 1,
                BookStatus.AVAILABLE, shelfFew));

        Room result = roomService.findRoomWithFewestBooks();
        int fewCount = roomService.countBooksInRoom(roomFew.getRoomId());
        int manyCount = roomService.countBooksInRoom(roomMany.getRoomId());
        int resultCount = roomService.countBooksInRoom(result.getRoomId());

        assertTrue(fewCount < manyCount);
        assertTrue(resultCount <= fewCount);
    }
}
