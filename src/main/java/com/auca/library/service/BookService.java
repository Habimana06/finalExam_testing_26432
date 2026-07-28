package com.auca.library.service;

import java.util.UUID;

import org.hibernate.SessionFactory;

import com.auca.library.dao.BookDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.domain.Book;
import com.auca.library.domain.Shelf;

public class BookService {

    private BookDao bookDao;
    private ShelfDao shelfDao;

    public BookService(SessionFactory sessionFactory) {
        this.bookDao = new BookDao(sessionFactory);
        this.shelfDao = new ShelfDao(sessionFactory);
    }

    // save a book
    public Book saveBook(Book book) {
        if (book.getBookId() == null) {
            book.setBookId(UUID.randomUUID());
        }
        return bookDao.save(book);
    }

    // assign book to a shelf and increase available stock
    public void assignBookToShelf(UUID bookId, UUID shelfId) {
        Book book = bookDao.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        Shelf shelf = shelfDao.findById(shelfId);
        if (shelf == null) {
            throw new IllegalArgumentException("Shelf not found");
        }

        book.setShelf(shelf);
        bookDao.update(book);

        shelf.setAvailableStock(shelf.getAvailableStock() + 1);
        shelf.setInitialStock(shelf.getInitialStock() + 1);
        shelfDao.update(shelf);
    }

    public Book findById(UUID bookId) {
        return bookDao.findById(bookId);
    }
}
