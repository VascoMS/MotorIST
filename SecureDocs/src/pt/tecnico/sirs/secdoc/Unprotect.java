package pt.tecnico.sirs.secdoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.util.SecurityUtil;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

//This will NOT be verifying the integrity of the files
public class Unprotect {
    private static final Logger logger = LoggerFactory.getLogger(Unprotect.class);

    public Unprotect() {}

    public ProtectedObject unprotect(ProtectedObject protectedObject, SecretKeySpec secretKey) {

        // Extract the content and iv from the json object
        String base64ContentCiphered = protectedObject.getContent();
        String iv = protectedObject.getIv();
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));

        String decipheredContent;
        try{
            // Decipher the content
            decipheredContent = SecurityUtil.decipherContent(base64ContentCiphered, ivSpec, secretKey);
        } catch (Exception e) {
            logger.error("Failed to decipher content: {}", e.getMessage());
            return null;
        }

        // Set the decipheredContent
        protectedObject.setContent(decipheredContent);

        return protectedObject;
    }
}
