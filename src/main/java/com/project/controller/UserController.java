package com.project.controller;

import com.project.entity.User;
import com.project.exception.CreateUserException;
import com.project.exception.InvalidInputException;
import com.project.exception.LoginException;
import com.project.exception.NullUserIdException;
import com.project.exception.NullUserUsernameException;
import com.project.service.UserService;
import java.util.Scanner;

public class UserController {

    private UserService userService;
    private Scanner scanner;

    public UserController(){
        this.userService = new UserService();
        this.scanner = new Scanner(System.in);
    }

    public UserController(UserService userService, Scanner scanner){
        this.userService = userService;
        this.scanner = scanner;
    }

    public int[] takeUserInput(){
        while(true){
            System.out.println("""
                    Please enter the number associated with the action you want to perform:
                    1 Create Account
                    2 Log in to your account
                    (Enter 'q' to exit at anytime);
                    """);
                    String userInput = scanner.nextLine();  
                    switch (userInput) {
                        case "1":
                            return createAccountHelper();
                        

                        case "2":
                            return LoginAccountHelper();
                            

                        case "q": 
                            int[] quit = {-1,-1};
                            return quit;
                        default:
                            throw new InvalidInputException("Invalid input: " + userInput);
            }
        }
    }

    public int[] createAccountHelper(){
        int[] userIdAndBoolean = new int[2];
        try {
            System.out.println("Enter your username");
            String username = scanner.nextLine();
            if (username.length() > 30){
                throw new CreateUserException("Username must not be longer than 30 characters");
            }
            System.out.println("Enter your password");
            String password = scanner.nextLine();
            if (password.length() > 30){
                throw new CreateUserException("Password must not be longer than 30 characters");
            }
            System.out.println("");
            User user = new User(username, password);
            User persistedUser = userService.createNewUser(user);
            User newUser = null;
            try {
                User tmpUser = userService.selectUserById(persistedUser.getId());
                newUser = tmpUser;
            } catch (NullUserIdException e){
                System.out.println(e.getMessage());
            }
            
            System.out.println("Welcome " + newUser.getUsername() + "! Your profile has been created!");
            userIdAndBoolean[0] = newUser.getId();
            userIdAndBoolean[1] = 1;
            return userIdAndBoolean;
        } catch (CreateUserException e){
            userIdAndBoolean[0] = 0;
            userIdAndBoolean[1] = 0;
            System.out.println(e.getMessage());
        }
        return userIdAndBoolean;
    }

    public int[] LoginAccountHelper(){
        int[] userIdAndBoolean = new int[2];
        try {
            System.out.println("Enter your username");
            String loginUsername = scanner.nextLine();
            System.out.println("Enter your password");
            String loginPassword = scanner.nextLine();
            String hashedLoginPassword = Integer.toString(User.hashCode(loginPassword));
            User loginUser = new User(loginUsername, hashedLoginPassword);
            userService.userLogin(loginUser);
            User getLoggedInUser = null;
            try {
                User tmpUser = userService.selectUserByUsername(loginUsername);
                getLoggedInUser = tmpUser;
            } catch (NullUserUsernameException e){
                System.out.println(e.getMessage());
            }
            
            userIdAndBoolean[0] = getLoggedInUser.getId();
            userIdAndBoolean[1] = 1;
            return userIdAndBoolean;
        } catch (LoginException e){
            userIdAndBoolean[0] = 0;
            userIdAndBoolean[1] = 0;
            System.out.println(e.getMessage());
        }
        return userIdAndBoolean;
    }

}

