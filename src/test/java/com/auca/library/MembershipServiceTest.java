package com.auca.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import com.auca.library.domain.Location;
import com.auca.library.domain.Membership;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.Status;
import com.auca.library.domain.User;
import com.auca.library.exception.BusinessRuleViolationException;

public class MembershipServiceTest extends TestBase {

    @Test
    public void registerMembership_gold_createsPendingMembershipLinkedToGoldType() {
        Location village = createFullHierarchy("MG" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("gold_" + UUID.randomUUID().toString().substring(0, 5), "pass123", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);

        Membership membership = membershipService.registerMembership(user.getPersonId(), gold.getMembershipTypeId());

        // membership starts as pending until librarian approves
        assertNotNull(membership.getMembershipId());
        assertNotNull(membership.getMembershipCode());
        assertEquals(Status.PENDING, membership.getMembershipStatus());
        assertEquals("Gold", membership.getMembershipType().getMembershipName());
        assertEquals(5, membership.getMembershipType().getMaxBooks());
        assertEquals(50, membership.getMembershipType().getPrice());
        assertEquals(user.getPersonId(), membership.getReader().getPersonId());
    }

    @Test(expected = BusinessRuleViolationException.class)
    public void registerMembership_userAlreadyHasActiveMembership_throwsException() {
        Location village = createFullHierarchy("MA" + UUID.randomUUID().toString().substring(0, 4));
        User user = createUser("active_" + UUID.randomUUID().toString().substring(0, 5), "pass", village);
        MembershipType gold = createMembershipType("Gold", 5, 50);
        MembershipType silver = createMembershipType("Silver", 3, 30);

        membershipService.registerMembership(user.getPersonId(), gold.getMembershipTypeId());
        // second registration should fail because pending already exists
        membershipService.registerMembership(user.getPersonId(), silver.getMembershipTypeId());
    }
}
