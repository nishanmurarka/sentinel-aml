package com.meridiantrust.sentinel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer {
    @Id
    private String id;
    private String name;
    private String riskRating;
    private String kycStatus;

    public Customer() {}

    public Customer(String id, String name, String riskRating, String kycStatus) {
        this.id = id;
        this.name = name;
        this.riskRating = riskRating;
        this.kycStatus = kycStatus;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String riskRating() { return riskRating; }
    public String kycStatus() { return kycStatus; }
}
