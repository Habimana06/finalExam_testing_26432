package com.auca.library.service;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.SessionFactory;

import com.auca.library.dao.MembershipDao;
import com.auca.library.dao.MembershipTypeDao;
import com.auca.library.dao.UserDao;
import com.auca.library.domain.Membership;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.Status;
import com.auca.library.domain.User;

public class MembershipService {

    private MembershipDao membershipDao;
    private MembershipTypeDao membershipTypeDao;
    private UserDao userDao;

    public MembershipService(SessionFactory sessionFactory) {
        this.membershipDao = new MembershipDao(sessionFactory);
        this.membershipTypeDao = new MembershipTypeDao(sessionFactory);
        this.userDao = new UserDao(sessionFactory);
    }

    // register membership for a user (status starts as PENDING)
    public Membership registerMembership(UUID userId, UUID membershipTypeId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        MembershipType type = membershipTypeDao.findById(membershipTypeId);
        if (type == null) {
            throw new IllegalArgumentException("Membership type not found");
        }

        Membership existing = membershipDao.findActiveByUserId(userId);
        if (existing != null) {
            throw new IllegalArgumentException("User already has an active membership");
        }

        Membership membership = new Membership();
        membership.setMembershipId(UUID.randomUUID());
        membership.setMembershipCode("MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        membership.setMembershipStatus(Status.PENDING);
        membership.setRegistrationDate(LocalDate.now());
        membership.setExpiryTime(LocalDate.now().plusYears(1));
        membership.setReader(user);
        membership.setMembershipType(type);

        return membershipDao.save(membership);
    }

    // approve membership (used by librarian / tests)
    public Membership approveMembership(UUID membershipId) {
        Membership membership = membershipDao.findById(membershipId);
        if (membership == null) {
            throw new IllegalArgumentException("Membership not found");
        }
        membership.setMembershipStatus(Status.APPROVED);
        return membershipDao.update(membership);
    }

    // save membership type like Gold, Silver, Striver
    public MembershipType saveMembershipType(MembershipType type) {
        if (type.getMembershipTypeId() == null) {
            type.setMembershipTypeId(UUID.randomUUID());
        }
        return membershipTypeDao.save(type);
    }

    public Membership findApprovedByUserId(UUID userId) {
        return membershipDao.findApprovedByUserId(userId);
    }

    public Membership findById(UUID membershipId) {
        return membershipDao.findById(membershipId);
    }
}
