package com.auca.library;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.Test;

import com.auca.library.domain.Book;
import com.auca.library.domain.BookStatus;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;

public class ShelfRoomTest extends TestBase {

    @Test
    public void assignBookToShelf_updatesBookShelfId() {
        Room room = roomService.saveRoom(new Room(UUID.randomUUID(), "ROOM-A"));
        Shelf shelf = roomService.saveShelf(new Shelf(UUID.randomUUID(), "Science", 0, 0, 0, room));
        Book book = bookService.saveBook(new Book(UUID.randomUUID(), "Physics", "ISBN-PHY",
                "AUCA", LocalDate.of(2019, 5, 1), 1, BookStatus.AVAILABLE, null));

        bookService.assignBookToShelf(book.getBookId(), shelf.getShelfId());

        Book updated = bookService.findById(book.getBookId());
        assertEquals(shelf.getShelfId(), updated.getShelf().getShelfId());
    }

    @Test
    public void assignBookToShelf_incrementsShelfAvailableStock() {
        Room room = roomService.saveRoom(new Room(UUID.randomUUID(), "ROOM-B"));
        Shelf shelf = roomService.saveShelf(new Shelf(UUID.randomUUID(), "Math", 0, 0, 0, room));
        Book book = bookService.saveBook(new Book(UUID.randomUUID(), "Algebra", "ISBN-ALG",
                "AUCA", LocalDate.of(2018, 3, 1), 1, BookStatus.AVAILABLE, null));

        int before = roomService.findShelfById(shelf.getShelfId()).getAvailableStock();
        bookService.assignBookToShelf(book.getBookId(), shelf.getShelfId());
        int after = roomService.findShelfById(shelf.getShelfId()).getAvailableStock();

        assertEquals(before + 1, after);
    }

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

        Book b1 = bookService.saveBook(new Book(UUID.randomUUID(), "Book1", "ISBN1", "P", LocalDate.of(2020, 1, 1), 1,
                BookStatus.AVAILABLE, shelf1));
        Book b2 = bookService.saveBook(new Book(UUID.randomUUID(), "Book2", "ISBN2", "P", LocalDate.of(2020, 1, 1), 1,
                BookStatus.AVAILABLE, shelf1));
        Book b3 = bookService.saveBook(new Book(UUID.randomUUID(), "Book3", "ISBN3", "P", LocalDate.of(2020, 1, 1), 1,
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
        // roomFew has 1 book, roomMany has 3 — but other tests may have created rooms.
        // so we only assert that the returned room is not null and has the lowest among known rooms
        int fewCount = roomService.countBooksInRoom(roomFew.getRoomId());
        int manyCount = roomService.countBooksInRoom(roomMany.getRoomId());
        int resultCount = roomService.countBooksInRoom(result.getRoomId());

        org.junit.Assert.assertTrue(fewCount < manyCount);
        org.junit.Assert.assertTrue(resultCount <= fewCount);
    }
}
