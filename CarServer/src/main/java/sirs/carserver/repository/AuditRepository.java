package sirs.carserver.repository;

import sirs.carserver.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<Audit, String> {
    //TODO add stuff
    Audit findByUsername(String username);
}
