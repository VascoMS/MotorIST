package sirs.motorist.cli;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Scanner;

import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import pt.tecnico.sirs.model.Nonce;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.secdoc.Check;
import pt.tecnico.sirs.secdoc.Protect;
import pt.tecnico.sirs.secdoc.Unprotect;
import pt.tecnico.sirs.util.JSONUtil;
import pt.tecnico.sirs.util.SecurityUtil;
import sirs.motorist.cli.model.Config;
import sirs.motorist.cli.model.dto.*;

import javax.crypto.spec.SecretKeySpec;

public class UserCLI {
    private static final String MANUFACTURER_URL = "http://localhost:8443/";
    private static final int NONCE_SIZE = 8;
    private static String username;
    private static String password;
    private static String carId;
    private static String keyStorePath;
    private static final Check check = new Check();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to the Motorist CLI");
            System.out.println("Choose what to do: ");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            String command = scanner.nextLine();

            try {
                switch (command) {
                    case "1" -> insertCredentials(scanner);
                    case "2" -> registerNewUser(scanner);
                    case "3" -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid command");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void userCLI(Scanner scanner) {
        while(true) {
            System.out.println("Choose a command: ");
            System.out.println("1. Pair a car");
            System.out.println("2. Get a configuration");
            System.out.println("3. Update a configuration");
            System.out.println("4. Delete a configuration");
            System.out.println("5. Get general car information");
            System.out.println("6. Change car");
            System.out.println("7. Logout");

            String command = scanner.nextLine();

            try {
                switch (command) {
                    case "1" -> pairCar(scanner);
                    case "2" -> getConfig();
                    case "3" -> updateConfig(scanner);
                    case "4" -> deleteConfig(scanner);
                    case "5" -> generalCarInfo();
                    case "6" -> changeCar(scanner);
                    case "7" -> {
                        System.out.println("Logging out...");
                        return;
                    }
                    default -> System.out.println("Invalid command");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void mechanicCLI(Scanner scanner) {
        while(true) {
            System.out.println("Choose a command: ");
            System.out.println("1. Download the firmware");
            System.out.println("2. Logout");

            String command = scanner.nextLine();

            try {
                switch (command) {
                    case "1" -> downloadFirmware();
                    case "2" -> {
                        System.out.println("logging out...");
                        return;
                    }
                    default -> System.out.println("Invalid command");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void insertCredentials(Scanner scanner) {
        System.out.println("Select a role: ");
        System.out.println("1. Mechanic");
        System.out.println("2. User");

        int role = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter username: ");
        username = scanner.nextLine();
        System.out.print("Password: ");
        password = scanner.nextLine();

        keyStorePath = String.format("keystore/%s.p12", username);

        changeCar(scanner);

        if (role == 1) {
            mechanicCLI(scanner);
        } else {
            userCLI(scanner);
        }
    }

    private static void registerNewUser(Scanner scanner) throws IOException {
        System.out.print("Username: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        String url = MANUFACTURER_URL + "/user/newUser";
        UserCredentialsDto dto = new UserCredentialsDto(name, pass);
        String body = JSONUtil.parseClassToJsonString(dto);

        HttpResponse response = HttpClientManager.executeHttpRequest(url, "POST", body);

        if(response != null) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        } else {
            System.out.println("Error registering new user");
        }
    }

    private static void changeCar(Scanner scanner) {
        System.out.print("Enter the chassis number: ");
        carId = scanner.nextLine();
    }

    private static void pairCar(Scanner scanner) throws Exception {
        System.out.print("Enter the pair code: ");
        String pairCode = scanner.nextLine();

        String url = MANUFACTURER_URL + "/user/pair";

        Nonce nonce = SecurityUtil.generateNonce(NONCE_SIZE);

        UserPairRequestDto dto = new UserPairRequestDto(username, password, nonce, carId, pairCode);
        String body = JSONUtil.parseClassToJsonString(dto);

        HttpResponse response = HttpClientManager.executeHttpRequest(url, "POST", body);

        if(response == null) {
            System.out.println("Failed to contact server...");
            return;
        }

        if(response.getStatusLine().getStatusCode() == 200) {
            System.out.print("Enter the new secret key: ");
            String inputtedSecretKey = scanner.nextLine();

            byte[] secretKeyBytes = Base64.getDecoder().decode(inputtedSecretKey);

            // Load the key store
            KeyStore keyStore = SecurityUtil.loadOrCreateKeyStore(password, keyStorePath);

            SecurityUtil.saveSecretKeyInKeyStore(keyStore, secretKeyBytes, carId, password, keyStorePath);

            System.out.println("Secret key stored successfully");
        } else {
            System.out.println("Pairing failed: " + EntityUtils.toString(response.getEntity()));
        }
    }

    private static void getConfig() throws Exception {
        String url = MANUFACTURER_URL + "/user/readConfig";
        sendRequestAndCheckResponse(carId, url, "User Configuration");
    }

    private static void updateConfig(Scanner scanner) throws Exception {
        System.out.print("Enter AC out1 value: ");
        int out1 = scanner.nextInt();

        System.out.print("Enter AC out2 value: ");
        int out2 = scanner.nextInt();

        System.out.print("Enter Seat pos1 value: ");
        int pos1 = scanner.nextInt();

        System.out.print("Enter Seat pos3 value: ");
        int pos3 = scanner.nextInt();

        // Clear the newline from the buffer
        scanner.nextLine();

        String url = MANUFACTURER_URL + "/user/updateConfig";

        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(password, keyStorePath);

        SecretKeySpec secretKeySpec = SecurityUtil.loadSecretKeyFromKeyStore(carId, password, keyStore);

        Config config = new Config(out1,out2,pos1,pos3);
        Protect protect = new Protect();

        ProtectedObject protectedObj = protect.protect(secretKeySpec, config, true);

        ConfigurationDto dto = new ConfigurationDto(
                username,
                carId,
                password,
                protectedObj.getContent(),
                protectedObj.getIv(),
                protectedObj.getNonce(),
                protectedObj.getHmac()
        );
        String body = JSONUtil.parseClassToJsonString(dto);

        HttpResponse response = HttpClientManager.executeHttpRequest(url, "PUT", body);

        if(response == null) {
            System.out.println("Failed to contact server...");
            return;
        }

        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    private static void deleteConfig(Scanner scanner) throws Exception {
        System.out.println("Please confirm the operation by typing \"DELETE <username>\": ");
        String confirmationPhrase = scanner.nextLine();

        String url = MANUFACTURER_URL + "/user/deleteConfig";

        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(password, keyStorePath);

        SecretKeySpec secretKeySpec = SecurityUtil.loadSecretKeyFromKeyStore(carId, password, keyStore);

        Protect protect = new Protect();

        ProtectedObject protectedConfirmationPhrase = protect.protect(secretKeySpec, confirmationPhrase, true);

        DeleteConfigDto dto = new DeleteConfigDto(
                username,
                carId,
                password,
                protectedConfirmationPhrase.getContent(),
                protectedConfirmationPhrase.getIv(),
                protectedConfirmationPhrase.getNonce(),
                protectedConfirmationPhrase.getHmac()
        );
        String body = JSONUtil.parseClassToJsonString(dto);

        HttpResponse response = HttpClientManager.executeHttpRequest(url, "PUT", body);

        if(response == null) {
            System.out.println("Failed to contact server...");
            return;
        }

        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    private static void generalCarInfo() throws Exception {
        String url = MANUFACTURER_URL + "/user/readCarInfo";
        sendRequestAndCheckResponse(carId, url, "Car Info");
    }

    private static void sendRequestAndCheckResponse(String carId, String url, String contentLabel) throws Exception {
        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(password, keyStorePath);

        System.out.println("Storing key: " + carId + "_secret");

        SecretKeySpec secretKeySpec = SecurityUtil.loadSecretKeyFromKeyStore(carId, password, keyStore);

        Nonce nonce = SecurityUtil.generateNonce(NONCE_SIZE);

        InfoGetterDto dto = new InfoGetterDto(username, carId, password, nonce);
        String body = JSONUtil.parseClassToJsonString(dto);

        HttpResponse response = HttpClientManager.executeHttpRequest(url, "POST", body);
        if(response == null) {
            System.out.println("Failed to contact server...");
            return;
        }
        if(response.getStatusLine().getStatusCode() != 200) {
            System.out.println("Error: " + EntityUtils.toString(response.getEntity()));
            return;
        }

        JsonObject resJsonObj = JSONUtil.parseJsonToClass(EntityUtils.toString(response.getEntity()), JsonObject.class);
        resJsonObj.remove("userId");
        resJsonObj.remove("carId");

        Unprotect unprotect = new Unprotect();
        ProtectedObject protectedObject = JSONUtil.parseJsonToClass(resJsonObj, ProtectedObject.class);
        protectedObject = unprotect.unprotect(protectedObject, secretKeySpec);

        boolean configValid = check.check(protectedObject, secretKeySpec, false);
        Config config = deserializeIntoObject(protectedObject);

        if (configValid) {
            System.out.println(contentLabel + " is valid");
            System.out.println(contentLabel + ": " + config.toString());
        }
        else {
            System.out.println("The integrity of the " + contentLabel + " was compromised");
        }
    }

    private static void downloadFirmware() throws Exception {
        String url = MANUFACTURER_URL + "/firmware/download";

        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(password, keyStorePath);

        // Get the private key
        PrivateKey privateKey = SecurityUtil.loadPrivateKeyFromKeyStore(username, password, keyStore);

        // Generate a nonce
        Nonce nonce = SecurityUtil.generateNonce(NONCE_SIZE);

        // Serialize the nonce if mechanic
        byte[] nonceBytes = SecurityUtil.serializeToByteArray(nonce);

        // Sign the data
        String signedData = SecurityUtil.signData(carId.getBytes(), privateKey, nonceBytes, null);

        // Create the request DTO
        FirmwareRequestDto dto = new FirmwareRequestDto(username, signedData, nonce, carId);
        String body = JSONUtil.parseClassToJsonString(dto);

        HttpResponse response = HttpClientManager.executeHttpRequest(url, "POST", body);

        if(response != null && response.getStatusLine().getStatusCode() == 200) {
            System.out.println(EntityUtils.toString(response.getEntity()));

            try (FileWriter writer = new FileWriter("files/firmware.json")) {
                // Serialize the object to JSON and write it to a file
                JSONUtil.serializeAndWriteToFile(response, writer); //TODO: check if the response is a SignedFirmwareDto
                System.out.println("JSON written to file.");
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }
        } else {
            System.out.println("Error downloading firmware");
        }
    }

    private static <T extends Serializable> T deserializeIntoObject(ProtectedObject protectedObject) throws IOException, ClassNotFoundException {
        byte[] contentBytes = Base64.getDecoder().decode(protectedObject.getContent());
        return SecurityUtil.deserializeFromByteArray(contentBytes);
    }
}