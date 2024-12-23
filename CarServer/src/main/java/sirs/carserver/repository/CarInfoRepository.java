package sirs.carserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sirs.carserver.model.GeneralCarInfo;

import java.util.Optional;

public interface CarInfoRepository extends JpaRepository<GeneralCarInfo, String> {
}
