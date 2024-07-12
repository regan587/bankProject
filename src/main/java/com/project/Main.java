package com.project;

import com.project.controllers.CheckingAccountController;
import com.project.controllers.TransferController;
import com.project.controllers.UserController;
import com.project.exceptions.CreateUserException;
import com.project.exceptions.InvalidInputException;

public class Main { 
    public static void main(String[] args) {
        UserController userController = new UserController();
        CheckingAccountController checkingAccountController = new CheckingAccountController();
        TransferController transferController = new TransferController();
        System.out.println("Welcome to Banking App! ");
        while (true) {
            try {
                int[] userResult = userController.takeUserInput();
                if (userResult == null || userResult[0] == -1) {
                    break; // Quit the app
                }

                int userId = userResult[0];
                if (userId > 0) {
                    boolean userWantsToQuit = false;
                    System.out.println("Welcome to your account!");
                    while (true) {
                        try {
                            try {
                                int[] accountResult = checkingAccountController.takeUserInput(userId);
                                if (accountResult == null) {
                                    userWantsToQuit = true; // User chose to quit
                                    break;
                                } else if (accountResult[0] == -1){
                                    continue;
                                }

                                int accountId = accountResult[0];
                                int action = accountResult[1];
                                if (action == 0) {
                                    break; // Logout and return to the initial login screen
                                }

                                boolean transferResult = transferController.takeUserInput(accountId, String.valueOf(action));
                                if (!transferResult) {
                                    userWantsToQuit = true; // User chose to quit
                                    break;
                                }
                            } catch (InvalidInputException e ){
                                System.out.println(e.getMessage());
                            }
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    if (userWantsToQuit) {
                        break; // Quit the app
                    }
                }
            } catch (InvalidInputException | CreateUserException e ) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Thank you for using the Banking App. Goodbye!");
    }
}
