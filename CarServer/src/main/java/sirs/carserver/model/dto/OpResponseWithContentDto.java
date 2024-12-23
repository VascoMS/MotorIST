package sirs.carserver.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.ProtectedObject;
import sirs.carserver.model.GeneralCarInfo;

@Getter
public class OpResponseWithContentDto extends OpResponseDto {
    private final String content;
    private final String iv;
    private final String hmac;

    public OpResponseWithContentDto(String reqId, boolean success, ProtectedObject protectedObject){
        super(reqId, success);
        this.iv = protectedObject.getIv();
        this.hmac = protectedObject.getHmac();
        this.content = protectedObject.getContent();
    }
}
