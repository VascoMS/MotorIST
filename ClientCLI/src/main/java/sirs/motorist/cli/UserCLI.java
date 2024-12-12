package sirs.motorist.cli;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Scanner;

import com.google.gson.Gson;
import sirs.motorist.cli.dto.FirmwareRequestDto;
import sirs.motorist.cli.sec.SecurityUtil;

public class UserCLI {

    private static final String CAR_URL = "http://localhost:8080/api"; //TODO: change to actual URL
    private static final String MANUFACTURER_URL = "http://localhost:8081/api"; //TODO: change to actual URL
    private static final Gson gson = new Gson();
    private static String username;
    private static String password;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a role: ");
        System.out.println("1. User");
        System.out.println("2. Mechanic");
        String role = scanner.nextLine();

        System.out.print("Enter username: ");
        username = scanner.nextLine();
        System.out.println("Password: ");
        password = scanner.nextLine();

        while (true) {
            if (role.equals("1")) {
                userCLI(scanner);
            } else if (role.equals("2")) {
                mechanicCLI(scanner);
            } else {
                System.out.println("Invalid role");
                return;
            }
        }
    }

    private static void userCLI(Scanner scanner) {
        System.out.println("Choose a command: ");
        System.out.println("1. Get a configuration");
        System.out.println("2. Create a new configuration");
        System.out.println("3. Update a configuration");
        System.out.println("4. Delete a configuration");
        System.out.println("5. Exit");

        int command = scanner.nextInt();
        scanner.nextLine();

        try {
            switch (command) {
                case 1:
                    getConfig();
                    break;
                case 2:
                    createConfig(scanner);
                    break;
                case 3:
                    updateConfig(scanner);
                    break;
                case 4:
                    deleteConfig();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid command");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void mechanicCLI(Scanner scanner) {
        System.out.println("Choose a command: ");
        System.out.println("1. Download the firmware");
        System.out.println("2. Exit");

        int command = scanner.nextInt();
        scanner.nextLine();

        try {
            switch (command) {
                case 1:
                    System.out.println("Enter the chassis number: ");
                    String chassisNumber = scanner.nextLine();
                    downloadFirmware(chassisNumber);
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

    private static void getConfig() throws Exception {
        //TODO: Implement method
    }

    private static void createConfig(Scanner scanner) throws Exception {
        //TODO: Implement method
    }

    private static void updateConfig(Scanner scanner) throws Exception {
        //TODO: Implement method
    }

    private static void deleteConfig() throws Exception {
        //TODO: Implement method
    }

    private static void downloadFirmware(String chassisNumber) throws Exception {
        String url = MANUFACTURER_URL + "/firmware/download";

        // Load the keystore
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream is = UserCLI.class.getClassLoader().getResourceAsStream(String.format("%s.jks", username))) {
            keyStore.load(is, password.toCharArray());
        }

        // Get the private key
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(username, password.toCharArray());

        // Sign the chassis number
        String signedChassisNumber = SecurityUtil.signData(chassisNumber.getBytes(), privateKey, null);

        // Create the request DTO
        FirmwareRequestDto dto = new FirmwareRequestDto(username, signedChassisNumber, chassisNumber);
        String body = gson.toJson(dto);

        String response = HttpClientManager.executeHttpRequest(url, "POST", body);

        System.out.println(response);
    }
}