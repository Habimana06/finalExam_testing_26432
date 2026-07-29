package com.auca.library;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.Test;

import com.auca.library.domain.Book;
import com.auca.library.domain.BookStatus;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;

// tests for BookService only
public class BookServiceTest extends TestBase {

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
}
