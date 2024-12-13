package pt.tecnico.sirs;

import pt.tecnico.sirs.secdoc.Check;
import pt.tecnico.sirs.secdoc.Protect;
import pt.tecnico.sirs.secdoc.Unprotect;

import java.util.Scanner;

public class SecureDocs {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("SecureDocs CLI. Type 'help' for a list of commands.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] inputArgs = input.split("\\s+");
            if (inputArgs.length < 1) {
                printUsage();
                continue;
            }

            String command = inputArgs[0];
            String[] commandArgs = java.util.Arrays.copyOfRange(inputArgs, 1, inputArgs.length);

            switch (command) {
                case "protect":
                    Protect p = new Protect();
                    p.protect(commandArgs);
                    break;
                case "unprotect":
                    Unprotect u = new Unprotect();
                    u.unprotect(commandArgs);
                    break;
                case "check":
                    Check c = new Check();
                    c.check(commandArgs);
                    break;
                case "help":
                    printUsage();
                    break;
                case "exit":
                    System.out.println("Exiting SecureDocs CLI.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Unknown command: " + command);
                    printUsage();
                    break;
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage: <command> [args...]");
        System.out.println("Commands:");
        System.out.println("  help      - Display this help message (^w^) uwu\n");
        System.out.println("  protect   - protect (input_file) (output_file) (secret_key)");
        System.out.println("  unprotect - unprotect (input_file) (output_file) (secret_key_path)");
        System.out.println("  check     - check (input_file) (sender_public_key)");
        System.out.println("  exit      - Exit the CLI");
    }
}