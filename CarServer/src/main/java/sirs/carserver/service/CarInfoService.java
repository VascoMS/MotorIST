package sirs.carserver.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.SecurityUtil;
import sirs.carserver.exception.InstallFirmwareException;
import sirs.carserver.model.GeneralCarInfo;
import sirs.carserver.model.dto.SignedFirmwareDto;
import sirs.carserver.repository.CarInfoRepository;

import java.security.PublicKey;

@Service
public class CarInfoService {

    private final CarInfoRepository carInfoRepository;
    private final KeyStoreService keyStoreService;
    @Value("${car.id}")
    private String carId;

    public CarInfoService(CarInfoRepository carInfoRepository, KeyStoreService keyStoreService) {
        this.carInfoRepository = carInfoRepository;
        this.keyStoreService = keyStoreService;
    }

    public GeneralCarInfo getCarInfo() {
        return carInfoRepository.findById(carId).orElse(null);
    }

    public void updateFirmware(SignedFirmwareDto signedFirmwareDto) throws Exception {
        PublicKey publicKey = keyStoreService.getPublicKey("manufacturer");

        // if the carId is not the same as the one in the signed data, the signature is invalid
        String signedData = signedFirmwareDto.getFirmwareVersion() + carId;

        boolean isValid = SecurityUtil.verifySignature(
                signedData.getBytes(),
                signedFirmwareDto.getManufacturerSignature(),
                publicKey,
                null,
                null
        );

        if(!isValid){
            throw new InstallFirmwareException("The signature is not valid");
        }

        GeneralCarInfo carInfo = carInfoRepository.findById(carId).orElse(null);
        if (carInfo == null) {
            throw new InstallFirmwareException("Car not found");
        } else if (carInfo.getFirmwareVersion() > signedFirmwareDto.getFirmwareVersion()) {
            throw new InstallFirmwareException("The firmware version is older than the current one");
        }

        carInfo.setFirmwareVersion(signedFirmwareDto.getFirmwareVersion());
        carInfoRepository.save(carInfo);
    }
}
