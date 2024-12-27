package sirs.carserver;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.carserver.exception.PairingSessionException;
import sirs.carserver.model.dto.SignedFirmwareDto;
import sirs.carserver.observer.Observer;
import sirs.carserver.service.CarInfoService;
import sirs.carserver.service.MessageProcessorService;
import sirs.carserver.service.PairingService;

import java.io.FileReader;
import java.util.Scanner;

@Component
class CarCommandLineRunner implements CommandLineRunner, Observer {
    private static final Logger logger = LoggerFactory.getLogger(CarCommandLineRunner.class);
    private final CarInfoService carInfoService;

    PairingService pairingService;

    public CarCommandLineRunner(PairingService pairingService, MessageProcessorService messageProcessorService, CarInfoService carInfoService){
        this.pairingService = pairingService;
        messageProcessorService.addObserver(this);
        this.carInfoService = carInfoService;
    }

    @Override
    public void run(String... args) {
        if (args.length > 0) {
            System.out.println("CLI Arguments: ");
            for (String arg : args) {
                System.out.println(arg);
            }
        } else {
            System.out.println("No CLI arguments provided.");
        }
        // Implement your CLI commands here
        System.out.println("Car Server is running in CLI mode!");
        carCLI();
    }

    private void carCLI() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("Choose a command: ");
            System.out.println("1. Pair with app");
            System.out.println("2. Install firmware");
            System.out.println("3. Exit");
            String command = scanner.nextLine();
            switch (command) {
                case "1" -> {
                    System.out.println("Starting pair, please input the following code in your app:");
                    pairWithApp();
                }
                case "2" -> {
                    System.out.println("Installing firmware started...");
                    installFirmware(scanner);
                }
                case "3" -> {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid command...");
            }
        }
    }

    private void pairWithApp(){
        try {
            String code = pairingService.createPairingSession();
            System.out.println("Pairing code: " + code);
        } catch (PairingSessionException e) {
            logger.error("Error creating pairing session {}", e.getMessage());
            System.out.println("Error creating pairing session...");
        }
    }

    private void installFirmware(Scanner scanner) {
        System.out.println("Enter the path to the firmware file: ");
        String firmwarePath = scanner.nextLine();

        try (FileReader reader = new FileReader(firmwarePath)) {
            JsonObject jsonObject = JSONUtil.parseFileReaderToJson(reader);
            SignedFirmwareDto firmware = JSONUtil.parseJsonToClass(jsonObject, SignedFirmwareDto.class);

            carInfoService.updateFirmware(firmware);

            System.out.println("Firmware installed successfully!");
        } catch (Exception e) {
            logger.error("Error while installing the firmware: {}", e.getMessage());
            System.out.println("Error while installing the firmware...");
        }
    }

    @Override
    public void update(boolean pairingSuccess) {
        // TODO: clear key from console output
        if(pairingSuccess){
            String key = pairingService.getKey();
            String output = "New secret key (DON'T SHARE WITH ANYONE): " + key;
            System.out.println(output);
        } else {
            System.out.println("Failed to pair...");
        }
    }

    public static void clearLine(int length) {
        System.out.print("\r"); // Move to the start of the line
        System.out.print(" ".repeat(length)); // Overwrite with spaces
        System.out.print("\r"); // Move back to the start
    }
}
