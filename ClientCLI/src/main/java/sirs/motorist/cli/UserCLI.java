package sirs.motorist.cli;

import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Scanner;

import com.google.gson.JsonObject;
import pt.tecnico.sirs.model.Nonce;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.secdoc.Check;
import pt.tecnico.sirs.secdoc.Protect;
import pt.tecnico.sirs.secdoc.Unprotect;
import pt.tecnico.sirs.util.JSONUtil;
import pt.tecnico.sirs.util.SecurityUtil;
import sirs.motorist.cli.model.dto.*;
import sirs.motorist.cli.model.Config;

import javax.crypto.spec.SecretKeySpec;

public class UserCLI {
    private static final String MANUFACTURER_URL = "http://localhost:8080/api"; //TODO: change to actual URL
    private static final int NONCE_SIZE = 8;
    private static String username;
    private static String password;
    private static final Check check = new Check();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to the Motorist CLI");
            System.out.println("Choose what to do: ");
            System.out.println("1. Login");
            System.out.println("2. Exit");

            int command = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (command) {
                    case 1:
                        insertCredentials(scanner);
                        break;
                    case 2:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid command");
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
            System.out.println("6. Logout");

            int command = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (command) {
                    case 1:
                        pairCar(scanner);
                        break;
                    case 2:
                        getConfig(scanner);
                        break;
                    case 3:
                        updateConfig(scanner);
                        break;
                    case 4:
                        deleteConfig(scanner);
                        break;
                    case 5:
                        generalCarInfo(scanner);
                        break;
                    case 6:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid command");
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

            int command = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (command) {
                    case 1:
                        downloadFirmware(scanner);
                        break;
                    case 2:
                        System.out.println("logging out...");
                        return;
                    default:
                        System.out.println("Invalid command");
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
        System.out.println("Password: ");
        password = scanner.nextLine();

        if (role == 1) {
            mechanicCLI(scanner);
        } else {
            userCLI(scanner);
        }
    }

    private static void pairCar(Scanner scanner) throws Exception {
        System.out.println("Enter the car chassis number: ");
        String carId = scanner.nextLine();
        System.out.println("Enter the pair code: ");
        String pairCode = scanner.nextLine();

        String url = MANUFACTURER_URL + "/car/pair";
        String keyStorePath = String.format("keystore/%s.jks", username);

        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(password, keyStorePath);

        Nonce nonce = SecurityUtil.generateNonce(NONCE_SIZE);

        UserPairRequestDto dto = new UserPairRequestDto(username, password, nonce, carId, pairCode);
        String body = JSONUtil.parseClassToJsonString(dto);

        String response = HttpClientManager.executeHttpRequest(url, "POST", body);

        System.out.println(response);

        System.out.println("Enter the new secret key: ");
        String inputtedSecretKey = scanner.nextLine();

        SecurityUtil.saveSecretKeyInKeyStore(keyStore, username, password, keyStorePath, inputtedSecretKey);

        System.out.println("Secret key stored successfully");
    }

    // TODO: extract to another method to avoid duplication
    private static void getConfig(Scanner scanner) throws Exception {
        System.out.println("Enter the car chassis number: ");
        String carId = scanner.nextLine();

        String url = MANUFACTURER_URL + "/user/readConfig";
        String keyStorePath = String.format("keystore/%s.jks", username);

        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(password, keyStorePath);

        SecretKeySpec secretKeySpec = SecurityUtil.loadSecretKeyFromKeyStore(username, password, keyStore);

        Nonce nonce = SecurityUtil.generateNonce(NONCE_SIZE);

        InfoGetterDto dto = new InfoGetterDto(username, carId, password, nonce);
        String body = JSONUtil.parseClassToJsonString(dto);

        String response = HttpClientManager.executeHttpRequest(url, "POST", body);

        JsonObject resJsonObj = JSONUtil.parseJsonToClass(response, JsonObject.class);
        resJsonObj.remove("userId");
        resJsonObj.remove("carId");

        Unprotect unprotect = new Unprotect();
        ProtectedObject protectedObject = JSONUtil.parseJsonToClass(resJsonObj, ProtectedObject.class);
        protectedObject = unprotect.unprotect(protectedObject, secretKeySpec);

        boolean configValid = check.check(protectedObject, secretKeySpec, false);

        String content = base64ToString(protectedObject);

        if (configValid) {
            System.out.println("Configuration is valid");
            System.out.println("Configuration: " + content);
        }
        else {
            System.out.println("The integrity of the configuration was compromised");
        }
    }

    private static void updateConfig(Scanner scanner) throws Exception {
        System.out.println("Enter the car chassis number: ");
        String carId = scanner.nextLine();

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
        String keyStorePath = String.format("keystore/%s.jks", username);

        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(password, keyStorePath);

        SecretKeySpec secretKeySpec = SecurityUtil.loadSecretKeyFromKeyStore(username, password, keyStore);

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

        String response = HttpClientManager.executeHttpRequest(url, "PUT", body);

        System.out.println(response);
    }

    private static void deleteConfig(Scanner scanner) throws Exception {
        System.out.println("Enter the car chassis number: ");
        String carId = scanner.nextLine();
        System.out.println("Please confirm the operation by typing \"DELETE <username>\": ");
        String confirmationPhrase = scanner.nextLine();

        String url = MANUFACTURER_URL + "/user/deleteConfig";
        String keyStorePath = String.format("keystore/%s.jks", username);

        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(password, keyStorePath);

        SecretKeySpec secretKeySpec = SecurityUtil.loadSecretKeyFromKeyStore(username, password, keyStore);

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

        String response = HttpClientManager.executeHttpRequest(url, "DELETE", body);

        System.out.println(response);
    }

    // TODO: extract to another method to avoid duplication
    private static void generalCarInfo(Scanner scanner) throws Exception {
        System.out.println("Enter the car chassis number: ");
        String carId = scanner.nextLine();

        String url = MANUFACTURER_URL + "/user/readCarInfo";
        String keyStorePath = String.format("keystore/%s.jks", username);

        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(password, keyStorePath);

        SecretKeySpec secretKeySpec = SecurityUtil.loadSecretKeyFromKeyStore(username, password, keyStore);

        Nonce nonce = SecurityUtil.generateNonce(NONCE_SIZE);

        InfoGetterDto dto = new InfoGetterDto(username, carId, password, nonce);
        String body = JSONUtil.parseClassToJsonString(dto);

        String response = HttpClientManager.executeHttpRequest(url, "POST", body);

        JsonObject resJsonObj = JSONUtil.parseJsonToClass(response, JsonObject.class);
        resJsonObj.remove("userId");
        resJsonObj.remove("carId");

        Unprotect unprotect = new Unprotect();
        ProtectedObject protectedObject = JSONUtil.parseJsonToClass(resJsonObj, ProtectedObject.class);
        protectedObject = unprotect.unprotect(protectedObject, secretKeySpec);

        boolean configValid = check.check(protectedObject, secretKeySpec, false);

        String content = base64ToString(protectedObject);

        if (configValid) {
            System.out.println("Car info is valid");
            System.out.println("Car info: " + content);
        }
        else {
            System.out.println("The integrity of the car info was compromised");
        }
    }

    private static void downloadFirmware(Scanner scanner) throws Exception {
        System.out.println("Enter the chassis number: ");
        String chassisNumber = scanner.nextLine();

        String url = MANUFACTURER_URL + "/firmware/download";
        String keyStorePath = String.format("keystore/%s.jks", username);

        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(password, keyStorePath); // TODO: Do we need to load the keystore everytime?

        // Get the private key
        PrivateKey privateKey = SecurityUtil.loadPrivateKeyFromKeyStore(username, password, keyStore);

        // Generate a nonce
        Nonce nonce = SecurityUtil.generateNonce(NONCE_SIZE);

        // Serialize the nonce if mechanico
        byte[] nonceBytes = SecurityUtil.serializeToByteArray(nonce);

        // Sign the data
        String signedData = SecurityUtil.signData(chassisNumber.getBytes(), privateKey, nonceBytes, null);

        // Create the request DTO
        FirmwareRequestDto dto = new FirmwareRequestDto(username, signedData, nonce, chassisNumber);
        String body = JSONUtil.parseClassToJsonString(dto);

        String response = HttpClientManager.executeHttpRequest(url, "POST", body);

        System.out.println(response);
    }

    private static String base64ToString(ProtectedObject protectedObject) throws IOException, ClassNotFoundException {
        byte[] contentBytes = Base64.getDecoder().decode(protectedObject.getContent());
        return SecurityUtil.deserializeFromByteArray(contentBytes);
    }
}