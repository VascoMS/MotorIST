package sirs.motorist.prototype.model.dto;

public class ConfigurationRequestDto {

    private String userId;
    private String userSignature;
    private String chassisNumber;

    public ConfigurationRequestDto(String userId, String userSignature, String chassisNumber) {
        this.userId = userId;
        this.userSignature = userSignature;
        this.chassisNumber = chassisNumber;
    }
}
