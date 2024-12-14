package service;

import model.Audit;
import model.Config;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.AuditRepository;

@Service
public class AuditService {
    @Autowired
    private AuditRepository auditRepository;

    public void createAudit(User user, Config config) {
        Audit audit = new Audit();
        audit.setUser(user);
        audit.setConfig(config);
        auditRepository.save(audit);
    }
}
