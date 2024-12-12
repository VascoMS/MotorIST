package com.sirs.carserver.repository;

import com.sirs.carserver.model.Firmware;
import com.sirs.carserver.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmwareRepository extends JpaRepository<Firmware, String> {
    // TODO: Add queries if needed
}
