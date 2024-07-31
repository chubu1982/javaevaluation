package com.bci.javaevaluation.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class User {
    @Id
    private UUID id;
    private Date created;
    private Date lastLogin;
    private String token;
    private Boolean isActive;
    private String name;
    private String email;
    private String password;
}
