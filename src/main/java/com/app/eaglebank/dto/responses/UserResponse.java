package com.app.eaglebank.dto.responses;

import com.app.eaglebank.model.Address;
import com.app.eaglebank.model.User;

import java.time.Instant;
import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String name;
    private Address address;
    private String phoneNumber;
    private String email;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.address = user.getAddress();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.createdTimestamp = user.getCreatedAt();
        this.updatedTimestamp = user.getUpdatedAt();
    }

    public UUID getId() { return id; }

    public String getName() { return name; }

    public Address getAddress() { return address; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getEmail() { return email; }

    public Instant getCreatedTimestamp() { return createdTimestamp; }

    public Instant getUpdatedTimestamp() { return updatedTimestamp; }
}

