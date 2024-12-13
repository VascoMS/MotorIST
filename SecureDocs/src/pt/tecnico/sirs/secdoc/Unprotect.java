package pt.tecnico.sirs.secdoc;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.sirs.secure.KeyManager;
import pt.tecnico.sirs.secure.ProtectedObjectBuilder;
import pt.tecnico.sirs.util.FileUtil;
import pt.tecnico.sirs.util.JSONUtil;
import pt.tecnico.sirs.util.SecurityUtil;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

//This will NOT be verifying the integrity of the files
public class Unprotect {

    private static final Logger logger = LoggerFactory.getLogger(Unprotect.class);

    public Unprotect() {}

    public void unprotect(String[] args) {
        // Check arguments
        if (args.length < 3) {
            logger.error("Argument(s) missing!");
            //Receiver = car | Sender = client
            logger.error("Usage: java {} input_file output_file secret_key_path", Unprotect.class.getName());
            return;
        }

        final String inputFilePath = args[0];
        final String outputFilePath = args[1];
        final String secretKeyPath = args[2];

        final KeyManager keyManager = new KeyManager();
        final SecretKeySpec secretKeySpec = keyManager.loadSecretKey(secretKeyPath);

        // Load the json object
        String fileContent = FileUtil.readContent(inputFilePath);
        JsonObject jsonObject = JSONUtil.parseJson(fileContent);

        // Extract the content and iv from the json object
        String base64ContentCiphered = jsonObject.get(ProtectedObjectBuilder.CONTENT_PROPERTY).getAsString();
        String iv = jsonObject.get(ProtectedObjectBuilder.IV_PROPERTY).getAsString();
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));

        String decipheredContent;
        try{
            // Decipher the content
            decipheredContent = SecurityUtil.decipherContent(base64ContentCiphered, ivSpec, secretKeySpec);
        } catch (Exception e) {
            logger.error("Failed to decipher content for file: {}", e.getMessage());
            return;
        }

        // Overwrite the content in the json object
        jsonObject.addProperty(ProtectedObjectBuilder.CONTENT_PROPERTY, decipheredContent);

        // Write to output file
        FileUtil.writeAsString(outputFilePath, JSONUtil.toJsonString(jsonObject));
    }
}
