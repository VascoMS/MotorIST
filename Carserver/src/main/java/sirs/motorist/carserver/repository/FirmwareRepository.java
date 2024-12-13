package sirs.motorist.carserver.repository;

import sirs.motorist.carserver.model.entity.Firmware;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmwareRepository extends JpaRepository<Firmware, String> {
    // TODO: Add queries if needed
}
