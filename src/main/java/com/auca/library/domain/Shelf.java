package com.auca.library.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shelf")
public class Shelf {

    @Id
    private UUID shelfId;

    private String bookCategory;

    private int initialStock;

    private int availableStock;

    private int borrowedNumber;

    @ManyToOne(fetch = jakarta.persistence.FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;

    public Shelf() {
    }

    public Shelf(UUID shelfId, String bookCategory, int initialStock, int availableStock, int borrowedNumber,
            Room room) {
        this.shelfId = shelfId;
        this.bookCategory = bookCategory;
        this.initialStock = initialStock;
        this.availableStock = availableStock;
        this.borrowedNumber = borrowedNumber;
        this.room = room;
    }

    public UUID getShelfId() {
        return shelfId;
    }

    public void setShelfId(UUID shelfId) {
        this.shelfId = shelfId;
    }

    public String getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(String bookCategory) {
        this.bookCategory = bookCategory;
    }

    public int getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(int initialStock) {
        this.initialStock = initialStock;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public int getBorrowedNumber() {
        return borrowedNumber;
    }

    public void setBorrowedNumber(int borrowedNumber) {
        this.borrowedNumber = borrowedNumber;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
