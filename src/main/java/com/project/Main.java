package com.project;

import com.project.controller.AccountController;
import com.project.controller.TransferController;
import com.project.controller.UserController;
import com.project.exception.CreateUserException;
import com.project.exception.DuplicateCheckingAccountNameException;
import com.project.exception.InvalidInputException;

public class Main { 
    public static void main(String[] args) {


        UserController userController = new UserController();
        AccountController checkingAccountController = new AccountController();
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

                            } catch (InvalidInputException | DuplicateCheckingAccountNameException e ){
                                System.out.println(e.getMessage());
                                System.out.println("");
                            }
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                            System.out.println("");
                        }
                    }
                    if (userWantsToQuit) {
                        break; // Quit the app
                    }
                }
            } catch (InvalidInputException | CreateUserException e ) {
                System.out.println(e.getMessage());
                System.out.println("");
                
            }
        }
        System.out.println("Thank you for using the Banking App. Goodbye!");
    }
}