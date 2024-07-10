package com.project.services;

import com.project.models.User;
import com.project.daos.UserDAO;
import com.project.exceptions.CreateUserException;
import com.project.exceptions.LoginException;
import com.project.exceptions.NullUserIdException;
import com.project.exceptions.NullUserUsernameException;

import java.util.List;

public class UserService {

    private UserDAO userDAO;

    public UserService(){
        userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }
    
    public User createNewUser(User newUser){
        if (checkUsernameIsUnique(newUser)){
            if (checkPasswordLength(newUser) && checkUsernameLength(newUser)){
                return userDAO.insertNewUser(newUser);
            } else if (!checkPasswordLength(newUser) && !checkUsernameLength(newUser)){
                throw new CreateUserException("Username and Password must be less than 30 characters");
            }
            else if (!checkPasswordLength(newUser)){
                throw new CreateUserException("Password must be less than 30 characters!");
            } else if (!checkUsernameLength(newUser)){
                throw new CreateUserException("Username must be less than 30 characters!");
            }
        } throw new CreateUserException("Username is taken!");
    }

    private boolean checkUsernameIsUnique(User newUser){
        List<User> users = userDAO.selectAllUsers();
        for (User user: users){
            if (newUser.getUsername().equals(user.getUsername())){
                return false;
            }
        }
        return true;
    }

    private boolean checkUsernameLength(User newUser){
        return newUser.getUsername().length() <= 30;
    }

    private boolean checkPasswordLength(User newUser){
        return newUser.getPassword().length() <= 30;
    }

    public User userLogin(User loginUser){
        List<User> users = userDAO.selectAllUsers();
        for (User user: users){
            if (user.getUsername().equals(loginUser.getUsername())
            && user.getPassword().equals(loginUser.getPassword())){
                System.out.println("");
                return loginUser;
            }
        }
        throw new LoginException("Invalid username/password combination!");
    }

    public User selectUserById(int id){
        User user = userDAO.selectUserById(id);
        if (user == null){
            throw new NullUserIdException("No user with id: " + id + " found!");
        }
        return user;
    }

    public User selectUserByUsername(String username){
        User user = userDAO.selectUserByUsername(username);
        if (user == null){
            throw new NullUserUsernameException("No user with username: " + username + "found!");
        }
        return user;
    }

    public void deleteUser(int id){
        User user = userDAO.selectUserById(id);
        if (user == null){
            throw new NullUserIdException("No user with id: " + id + " found!");
        }
        userDAO.deleteUserById(id);
        System.out.println("User with id " + id + " deleted!");
    }

}
