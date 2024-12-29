package sirs.motorist.prototype.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.SecurityUtil;
import sirs.motorist.prototype.model.dto.FirmwareRequestDto;
import sirs.motorist.prototype.model.dto.SignedFirmwareDto;
import sirs.motorist.prototype.model.entity.Firmware;
import sirs.motorist.prototype.model.entity.User;
import sirs.motorist.prototype.repository.FirmwareRepository;
import sirs.motorist.prototype.repository.UserRepository;
import sirs.motorist.prototype.service.FirmwareService;
import sirs.motorist.prototype.service.KeyStoreService;

import java.security.PrivateKey;
import java.security.PublicKey;

@Service
public class FirmwareServiceImpl implements FirmwareService {

    private static final Logger logger = LoggerFactory.getLogger(FirmwareServiceImpl.class);

    private final UserRepository userRepository;
    private final FirmwareRepository firmwareRepository;
    private final KeyStoreService keyStoreService;

    @Value("${keystore.manufacturer.alias}")
    private String privateKeyAlias;

    @Autowired
    public FirmwareServiceImpl(UserRepository userRepository, FirmwareRepository firmwareRepository, KeyStoreService keyStoreService) {
        this.userRepository = userRepository;
        this.firmwareRepository = firmwareRepository;
        this.keyStoreService = keyStoreService;
    }

    @Override
    public boolean checkMechanicSignature(FirmwareRequestDto firmwareDownloadRequest) {
        logger.info("Checking mechanic signature...");
        User mechanic = userRepository.findByUserId(firmwareDownloadRequest.getUserId());
        if (mechanic == null || !mechanic.isMechanic()) {
            logger.error("Mechanic not found...");
            return false;
        }
        String mechanicSignature = firmwareDownloadRequest.getMechanicSignature();
        // Chassis is what gets signed by the mechanic
        String signedData = firmwareDownloadRequest.getChassisNumber();
        PublicKey mechanicPublicKey = SecurityUtil.convertPublicKeyFromString(mechanic.getPublicKey());
        try {
            byte[] nonce = SecurityUtil.serializeToByteArray(firmwareDownloadRequest.getNonce());
            return SecurityUtil.verifySignature(signedData.getBytes(), mechanicSignature, mechanicPublicKey, nonce, null);
        } catch (Exception e) {
            logger.error("Error verifying mechanic signature... {}", e.getMessage());
            return false;
        }
    }

    @Override
    public SignedFirmwareDto fetchAndSignFirmware(String chassisNumber) {
        logger.info("Fetching and signing firmware...");
        Firmware latestFirmware = firmwareRepository.findTopByOrderByVersionDesc();
        try {
            PrivateKey privateKey = keyStoreService.getPrivateKey(privateKeyAlias);
            String dataToSign = latestFirmware.getVersion() + chassisNumber;
            String signedData = SecurityUtil.signData(dataToSign.getBytes(), privateKey, null, null);
            return new SignedFirmwareDto(signedData, latestFirmware.getVersion(), chassisNumber);
        } catch(Exception e) {
            logger.error("Error fetching and signing firmware... {}", e.getMessage());
            return null;
        }
    }
}
