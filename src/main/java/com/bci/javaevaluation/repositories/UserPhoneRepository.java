package com.bci.javaevaluation.repositories;

import com.bci.javaevaluation.domain.UserPhone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserPhoneRepository extends JpaRepository<UserPhone, UUID> {
    List<UserPhone> findAllByUserId(UUID userId);
}
