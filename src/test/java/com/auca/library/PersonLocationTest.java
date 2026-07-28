package com.auca.library;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.auca.library.domain.Location;
import com.auca.library.domain.User;

public class PersonLocationTest extends TestBase {

    @Test
    public void validPersonId_returnsCorrectProvinceName() {
        String prefix = "PP" + UUID.randomUUID().toString().substring(0, 4);
        Location village = createFullHierarchy(prefix);
        User user = createUser("person_" + UUID.randomUUID().toString().substring(0, 5), "pass123", village);

        String provinceName = locationService.getProvinceNameByPersonId(user.getPersonId());

        assertEquals(prefix + " Province", provinceName);
    }
}
