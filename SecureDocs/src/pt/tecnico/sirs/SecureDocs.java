package pt.tecnico.sirs;

import pt.tecnico.sirs.secdoc.Protect;
import pt.tecnico.sirs.secdoc.Unprotect;
import pt.tecnico.sirs.secdoc.Check;
import pt.tecnico.sirs.model.ProtectedObject;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SecureDocs {

    public static void main(String[] args) {
        try {
            // Example content to protect
            String content = "This is a test content";
            SecretKeySpec secretKey = new SecretKeySpec("1234567890123456".getBytes(StandardCharsets.UTF_8), "AES");

            // Generate a new RSA key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // Protect the content
            Protect protect = new Protect();
            ProtectedObject protectedObject = protect.protect(secretKey, content, privateKey);
            System.out.println("Protected Object: ");
            System.out.println("  - Content: " + protectedObject.getContent());
            System.out.println("  - IV: " + protectedObject.getIv());
            System.out.println("  - Nonce: " + protectedObject.getNonce());
            System.out.println("  - Signature: " + protectedObject.getSignature());

            // Unprotect the content
            Unprotect unprotect = new Unprotect();
            ProtectedObject unprotectedContent = unprotect.unprotect(protectedObject, secretKey);
            System.out.println("Unprotected Content:");
            System.out.println("  - Content: " + unprotectedContent.getContent());
            System.out.println("  - IV: " + unprotectedContent.getIv());
            System.out.println("  - Nonce: " + unprotectedContent.getNonce());
            System.out.println("  - Signature: " + unprotectedContent.getSignature());

            // Check the content
            Check check = new Check();
            boolean isValid = check.check(protectedObject, publicKey);
            System.out.println("Is the content valid? " + isValid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}