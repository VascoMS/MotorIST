package sirs.carserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sirs.carserver.exception.PairingSessionException;
import sirs.carserver.observer.Observer;
import sirs.carserver.service.MessageProcessorService;
import sirs.carserver.service.PairingService;

import java.util.Scanner;

@Component
class CarCommandLineRunner implements CommandLineRunner, Observer {
    private static final Logger logger = LoggerFactory.getLogger(CarCommandLineRunner.class);

    PairingService pairingService;

    public CarCommandLineRunner(PairingService pairingService, MessageProcessorService messageProcessorService){
        this.pairingService = pairingService;
        messageProcessorService.addObserver(this);
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
            System.out.println("2. Exit");
            int command = scanner.nextInt();
            scanner.nextLine();
            switch (command) {
                case 1 -> {
                    System.out.println("Starting pair, please input the following code in your app:");
                    pairWithApp();
                }
                case 2 -> {
                    System.out.println("Exiting...");
                    return;
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

    @Override
    public void update(boolean pairingSuccess) {
        // TODO: clear key from console output
        if(pairingSuccess){
            try {
                String key = pairingService.storeKey();
                String output = "New secret key (DON'T SHARE WITH ANYONE): " + key;
                System.out.println(output);
            } catch (PairingSessionException e){
                logger.error("Error storing key {}", e.getMessage());
                System.out.println("Error storing key...");
            }
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
