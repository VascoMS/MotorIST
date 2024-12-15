package sirs.carserver.service;

import sirs.carserver.model.Audit;
import sirs.carserver.model.Config;
import sirs.carserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sirs.carserver.repository.AuditRepository;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    @Autowired
    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void createAudit(User user, Config config) {
        Audit audit = new Audit();
        audit.setUser(user);
        audit.setConfig(config);
        auditRepository.save(audit);
    }
}
