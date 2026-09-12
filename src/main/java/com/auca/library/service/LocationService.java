package com.auca.library.service;

import java.util.UUID;

import org.hibernate.SessionFactory;

import com.auca.library.dao.LocationDao;
import com.auca.library.dao.UserDao;
import com.auca.library.domain.Location;
import com.auca.library.domain.LocationType;
import com.auca.library.domain.User;
import com.auca.library.exception.BusinessRuleViolationException;
import com.auca.library.exception.EntityNotFoundException;

public class LocationService {

    private LocationDao locationDao;
    private UserDao userDao;

    public LocationService(SessionFactory sessionFactory) {
        this.locationDao = new LocationDao(sessionFactory);
        this.userDao = new UserDao(sessionFactory);
    }

    // create location (province has no parent, others need parent)
    public Location createLocation(Location location, UUID parentId) {
        if (location.getLocationCode() != null && locationDao.findByCode(location.getLocationCode()) != null) {
            throw new BusinessRuleViolationException("Location code already exists");
        }

        if (location.getLocationType() == LocationType.PROVINCE) {
            location.setParent(null);
        } else {
            if (parentId == null) {
                throw new BusinessRuleViolationException("Parent location is required");
            }
            Location parent = locationDao.findById(parentId);
            if (parent == null) {
                throw new EntityNotFoundException("Parent location not found");
            }
            location.setParent(parent);
        }

        if (location.getLocationId() == null) {
            location.setLocationId(UUID.randomUUID());
        }

        return locationDao.save(location);
    }

    // get province name using village id
    public String getProvinceNameByVillageId(UUID villageId) {
        Location current = locationDao.findById(villageId);
        if (current == null) {
            throw new EntityNotFoundException("Village not found");
        }

        // walk up the parents until we find PROVINCE
        while (current != null && current.getLocationType() != LocationType.PROVINCE) {
            current = current.getParent();
            if (current != null && current.getLocationId() != null) {
                // reload to make sure parent is loaded
                current = locationDao.findById(current.getLocationId());
            }
        }

        if (current == null) {
            throw new EntityNotFoundException("Province not found for this village");
        }
        return current.getLocationName();
    }

    // get province name using person id
    public String getProvinceNameByPersonId(UUID personId) {
        User user = userDao.findById(personId);
        if (user == null) {
            throw new EntityNotFoundException("Person not found");
        }
        if (user.getVillage() == null) {
            throw new BusinessRuleViolationException("Person has no village");
        }
        return getProvinceNameByVillageId(user.getVillage().getLocationId());
    }
}
