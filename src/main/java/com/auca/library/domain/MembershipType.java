package com.auca.library.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "membership_type")
public class MembershipType {

    @Id
    private UUID membershipTypeId;

    private String membershipName;

    private int maxBooks;

    private int price;

    public MembershipType() {
    }

    public MembershipType(UUID membershipTypeId, String membershipName, int maxBooks, int price) {
        this.membershipTypeId = membershipTypeId;
        this.membershipName = membershipName;
        this.maxBooks = maxBooks;
        this.price = price;
    }

    public UUID getMembershipTypeId() {
        return membershipTypeId;
    }

    public void setMembershipTypeId(UUID membershipTypeId) {
        this.membershipTypeId = membershipTypeId;
    }

    public String getMembershipName() {
        return membershipName;
    }

    public void setMembershipName(String membershipName) {
        this.membershipName = membershipName;
    }

    public int getMaxBooks() {
        return maxBooks;
    }

    public void setMaxBooks(int maxBooks) {
        this.maxBooks = maxBooks;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
