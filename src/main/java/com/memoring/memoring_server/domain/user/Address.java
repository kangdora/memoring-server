package com.memoring.memoring_server.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Column(nullable = false, length = 10)
    private String zipcode;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(length = 100)
    private String addressDetail;

    public Address(String zipcode, String address, String addressDetail) {
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
    }
}
