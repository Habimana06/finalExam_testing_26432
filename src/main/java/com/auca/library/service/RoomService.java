package com.auca.library.service;

import java.util.List;
import java.util.UUID;

import org.hibernate.SessionFactory;

import com.auca.library.dao.BookDao;
import com.auca.library.dao.RoomDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;
import com.auca.library.exception.EntityNotFoundException;

public class RoomService {

    private RoomDao roomDao;
    private ShelfDao shelfDao;
    private BookDao bookDao;

    public RoomService(SessionFactory sessionFactory) {
        this.roomDao = new RoomDao(sessionFactory);
        this.shelfDao = new ShelfDao(sessionFactory);
        this.bookDao = new BookDao(sessionFactory);
    }

    // save a room
    public Room saveRoom(Room room) {
        if (room.getRoomId() == null) {
            room.setRoomId(UUID.randomUUID());
        }
        return roomDao.save(room);
    }

    // save a shelf
    public Shelf saveShelf(Shelf shelf) {
        if (shelf.getShelfId() == null) {
            shelf.setShelfId(UUID.randomUUID());
        }
        return shelfDao.save(shelf);
    }

    // assign shelf to a room
    public void assignShelfToRoom(UUID shelfId, UUID roomId) {
        Shelf shelf = shelfDao.findById(shelfId);
        if (shelf == null) {
            throw new EntityNotFoundException("Shelf not found");
        }

        Room room = roomDao.findById(roomId);
        if (room == null) {
            throw new EntityNotFoundException("Room not found");
        }

        shelf.setRoom(room);
        shelfDao.update(shelf);
    }

    // count how many books are in a room
    public int countBooksInRoom(UUID roomId) {
        Room room = roomDao.findById(roomId);
        if (room == null) {
            throw new EntityNotFoundException("Room not found");
        }
        return bookDao.countBooksInRoom(roomId);
    }

    // find the room that has the fewest books
    public Room findRoomWithFewestBooks() {
        List<Room> rooms = roomDao.findAll();
        if (rooms.isEmpty()) {
            return null;
        }

        Room fewest = null;
        int minCount = Integer.MAX_VALUE;

        for (Room room : rooms) {
            int count = bookDao.countBooksInRoom(room.getRoomId());
            if (count < minCount) {
                minCount = count;
                fewest = room;
            }
        }
        return fewest;
    }

    public Shelf findShelfById(UUID shelfId) {
        return shelfDao.findById(shelfId);
    }

    public Room findRoomById(UUID roomId) {
        return roomDao.findById(roomId);
    }
}
