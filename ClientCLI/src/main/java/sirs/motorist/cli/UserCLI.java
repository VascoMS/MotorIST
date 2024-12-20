package sirs.motorist.cli;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Scanner;

import com.google.gson.Gson;
import pt.tecnico.sirs.model.Nonce;
import pt.tecnico.sirs.util.SecurityUtil;
import sirs.motorist.cli.dto.FirmwareRequestDto;
import sirs.motorist.cli.dto.PairingRequestDto;

public class UserCLI {
    private static final String MANUFACTURER_URL = "http://localhost:8080/api"; //TODO: change to actual URL
    private static final Gson gson = new Gson();
    private static String username;
    private static String password;

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
            System.out.println("5. Logout");

            int command = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (command) {
                    case 1:
                        pairCar(scanner);
                        break;
                    case 2:
                        getConfig();
                        break;
                    case 3:
                        updateConfig(scanner);
                        break;
                    case 4:
                        deleteConfig();
                        break;
                    case 5:
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
        System.out.println("Enter the pair code: ");
        String pairCode = scanner.nextLine();

        String url = MANUFACTURER_URL + "/car/pair";
        String keyStorePath = String.format("keystore/%s.jks", username);

        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(username, password, keyStorePath);

        // Create the request DTO
        PairingRequestDto dto = new PairingRequestDto(username, pairCode);
        String body = gson.toJson(dto);

        String response = HttpClientManager.executeHttpRequest(url, "POST", body);

        System.out.println(response);

        System.out.println("Enter the new secret key: ");
        String inputtedSecretKey = scanner.nextLine();

        SecurityUtil.saveSecretKeyInKeyStore(keyStore, username, password, keyStorePath, inputtedSecretKey);

        System.out.println("Secret key stored successfully");
    }

    private static void getConfig() throws Exception {
        //TODO: Implement method
    }

    private static void updateConfig(Scanner scanner) throws Exception {
        //TODO: Implement method
    }

    private static void deleteConfig() throws Exception {
        //TODO: Implement method
    }

    private static void downloadFirmware(Scanner scanner) throws Exception {
        System.out.println("Enter the chassis number: ");
        String chassisNumber = scanner.nextLine();

        String url = MANUFACTURER_URL + "/firmware/download";
        String keyStorePath = String.format("keystore/%s.jks", username);

        // Load the key store
        KeyStore keyStore = SecurityUtil.loadKeyStore(username, password, keyStorePath); // TODO: Do we need to load the keystore everytime?

        // Get the private key
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(username + "_priv", password.toCharArray());

        // Generate a nonce
        Nonce nonce = SecurityUtil.generateNonce(8);

        // Serialize the nonce if mechanico
        byte[] nonceBytes = SecurityUtil.serializeToByteArray(nonce);

        // Sign the data
        String signedData = SecurityUtil.signData(chassisNumber.getBytes(), privateKey, nonceBytes);

        // Create the request DTO
        FirmwareRequestDto dto = new FirmwareRequestDto(username, signedData, nonce, chassisNumber);
        String body = gson.toJson(dto);

        String response = HttpClientManager.executeHttpRequest(url, "POST", body);

        System.out.println(response);
    }
}