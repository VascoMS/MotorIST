package pt.tecnico.sirs.secdoc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import pt.tecnico.sirs.secure.ProtectedObjectBuilder;
import pt.tecnico.sirs.util.FileUtil;
import pt.tecnico.sirs.secure.KeyManager;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Protect {

    private static final Logger logger = LoggerFactory.getLogger(Protect.class);

    public Protect() {}

    public void protect(String[] args) {

        // Check arguments
        if (args.length < 3) {
            logger.error("Argument(s) missing!");
            logger.error("Usage: java {} input_file output_file secret_key", Protect.class.getName());
            return;
        }

        final String inputFilePath = args[0];
        final String outputFilePath = args[1];
        final String secretKeyPath = args[2];

        final KeyManager keyManager = new KeyManager();
        ProtectedObjectBuilder protectedObjectBuilder = new ProtectedObjectBuilder();

        final SecretKeySpec secretKey = keyManager.loadSecretKey(secretKeyPath);

        // Read the data from the file
        byte[] content = FileUtil.readBytes(inputFilePath);

        //------------------------------BUILD PROTECTED OBJECT------------------------------------------
        JsonObject protectedObject;
        try {
            protectedObject = protectedObjectBuilder
                                .cipherContent(content, secretKey)
                                .generateNonce(8)
                                .generateHMAC(content, secretKey.getEncoded())
                                .build();
        } catch(Exception e){
            logger.error("Failed to build protected object for file: {}", inputFilePath);
            return;
        }

        //------------------------------WRITE TO OUTPUT FILE------------------------------------------

        writeProtectedDocument(outputFilePath, protectedObject);

    }

    public static void writeProtectedDocument(String filePath, JsonObject protectedObject){
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            // Convert JsonObject to JSON string and write to the file
            // TODO: Added prettyPrinting --> GsonBuilder().setPrettyPrinting().create();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            fileWriter.write(gson.toJson(protectedObject));
        } catch (Exception e) {
            logger.error("Writing the output file object failed {}", e.getMessage());
            System.exit(1);
        }
    }
}
