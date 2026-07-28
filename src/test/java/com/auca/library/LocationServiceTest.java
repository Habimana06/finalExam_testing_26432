package com.auca.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.junit.Test;

import com.auca.library.domain.Location;
import com.auca.library.domain.LocationType;

public class LocationServiceTest extends TestBase {

    @Test
    public void createProvince_withNoParent_succeeds() {
        Location province = new Location(UUID.randomUUID(), "KIG-PROV-" + UUID.randomUUID().toString().substring(0, 4),
                "Kigali", LocationType.PROVINCE, null);

        Location saved = locationService.createLocation(province, null);

        assertNotNull(saved.getLocationId());
        assertEquals(LocationType.PROVINCE, saved.getLocationType());
        assertNull(saved.getParent());
    }

    @Test
    public void createDistrict_withValidProvinceParent_succeeds() {
        Location province = new Location(UUID.randomUUID(), "PROV-" + UUID.randomUUID().toString().substring(0, 4),
                "Northern", LocationType.PROVINCE, null);
        province = locationService.createLocation(province, null);

        Location district = new Location(UUID.randomUUID(), "DIST-" + UUID.randomUUID().toString().substring(0, 4),
                "Musanze", LocationType.DISTRICT, null);
        Location saved = locationService.createLocation(district, province.getLocationId());

        assertNotNull(saved.getParent());
        assertEquals(province.getLocationId(), saved.getParent().getLocationId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDistrict_withMissingParent_throwsException() {
        Location district = new Location(UUID.randomUUID(), "DIST-MISS-" + UUID.randomUUID().toString().substring(0, 4),
                "NoParent", LocationType.DISTRICT, null);
        locationService.createLocation(district, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createLocation_duplicateLocationCode_throwsException() {
        String code = "DUP-" + UUID.randomUUID().toString().substring(0, 4);
        Location province1 = new Location(UUID.randomUUID(), code, "East", LocationType.PROVINCE, null);
        locationService.createLocation(province1, null);

        Location province2 = new Location(UUID.randomUUID(), code, "West", LocationType.PROVINCE, null);
        locationService.createLocation(province2, null);
    }

    @Test
    public void validVillageId_returnsCorrectProvinceName() {
        String prefix = "VP" + UUID.randomUUID().toString().substring(0, 4);
        Location village = createFullHierarchy(prefix);

        String provinceName = locationService.getProvinceNameByVillageId(village.getLocationId());

        assertEquals(prefix + " Province", provinceName);
    }
}
