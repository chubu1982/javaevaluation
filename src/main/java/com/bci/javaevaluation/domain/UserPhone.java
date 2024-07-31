package com.bci.javaevaluation.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
public class UserPhone {
    @Id
    private UUID id;
    private UUID userId;
    private long number;
    private int citycode;
    private String contrycode;
}
