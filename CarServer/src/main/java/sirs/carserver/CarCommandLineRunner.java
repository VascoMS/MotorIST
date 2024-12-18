package sirs.carserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sirs.carserver.config.CarWebSocketClient;
import sirs.carserver.service.PairingService;

import java.util.Scanner;

@Component
class CarCommandLineRunner implements CommandLineRunner {
    PairingService pairingService;
    CarWebSocketClient webSocketClient;

    public CarCommandLineRunner(PairingService pairingService, CarWebSocketClient webSocketClient){
        this.pairingService = pairingService;
        this.webSocketClient = webSocketClient;
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
        userCLI();
    }

    private void userCLI() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("Choose a command: ");
            System.out.println("1. Pair with app");
            System.out.println("2. Exit");
            int command = scanner.nextInt();
            scanner.nextLine();
            switch (command) {
                case 1:
                    System.out.println("Starting pair, please input the following code in your app:");
                    pairWithApp();
                    break;
                case 2:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid command...");
            }
        }
    }

    private void pairWithApp(){
        String code = pairingService.createPairingSession();

        System.out.println("Pairing code: " + code);


    }
}
