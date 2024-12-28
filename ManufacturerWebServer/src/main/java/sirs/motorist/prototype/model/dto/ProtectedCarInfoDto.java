package sirs.motorist.prototype.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectedCarInfoDto {
    private String content;
    private String iv;
    private String hmac;

    public ProtectedCarInfoDto(String content, String iv, String hmac) {
        this.content = content;
        this.iv = iv;
        this.hmac = hmac;
    }
}
