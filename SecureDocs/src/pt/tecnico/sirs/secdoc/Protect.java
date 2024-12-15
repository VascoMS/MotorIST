package pt.tecnico.sirs.secdoc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.PrivateKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.secure.ProtectedObjectBuilder;
import pt.tecnico.sirs.util.JSONUtil;
import pt.tecnico.sirs.util.SecurityUtil;

public class Protect {

    private static final Logger logger = LoggerFactory.getLogger(Protect.class);

    public Protect() {}

    public <T extends Serializable> ProtectedObject protect(SecretKeySpec secretKey, T content, PrivateKey privkey) throws IOException {

        ProtectedObjectBuilder protectedObjectBuilder = new ProtectedObjectBuilder();

        // Read the data from the file
        byte[] byteContent = SecurityUtil.serializeToByteArray(content);

        //------------------------------BUILD PROTECTED OBJECT------------------------------------------
        JsonObject protectedObject;
        try {
            protectedObject = protectedObjectBuilder
                    .cipherContent(byteContent, secretKey)
                    .generateNonce(8)
                    .signData(byteContent, privkey)
                    .build();
        } catch(Exception e){
            logger.error("Failed to build protected object: {}", e.getMessage());
            return null;
        }

        return JSONUtil.parseJsonToClass(protectedObject, ProtectedObject.class);
    }

    public static void writeProtectedDocument(String filePath, JsonObject protectedObject){
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            // Convert JsonObject to JSON string and write to the file
            // TODO: Added prettyPrinting --> GsonBuilder().setPrettyPrinting().create();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            fileWriter.write(gson.toJson(protectedObject));
        } catch (Exception e) {
            logger.error("Writing the output file object failed {}", e.getMessage());
        }
    }
}
