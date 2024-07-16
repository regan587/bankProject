package com.project.service;

import com.project.dao.UserDAO;
import com.project.entity.User;
import com.project.exception.CreateUserException;
import com.project.exception.LoginException;
import com.project.exception.NullUserIdException;
import com.project.exception.NullUserUsernameException;
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
                User hashedUser = new User(newUser.getUsername(),Integer.toString(User.hashCode(newUser.getPassword())));
                return userDAO.insertNewUser(hashedUser);
            } else if (!checkPasswordLength(newUser) && !checkUsernameLength(newUser)){
                throw new CreateUserException("Username and Password must be less than 30 characters");
            }
            else if (!checkPasswordLength(newUser)){
                System.out.println("");
                throw new CreateUserException("Password must be less than 30 characters!" + "\n");
            } else if (!checkUsernameLength(newUser)){
                System.out.println("");
                throw new CreateUserException("Username must be less than 30 characters!" + "\n");
            }
        } throw new CreateUserException("Username is taken!" + "\n");
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
        System.out.println("");
        throw new LoginException("Invalid username/password combination!" + "\n");
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

    // could remove deleteUser methods here and in DAO

    public void deleteUser(int id){
        User user = userDAO.selectUserById(id);
        if (user == null){
            throw new NullUserIdException("No user with id: " + id + " found!");
        }
        userDAO.deleteUserById(id);
        System.out.println("User with id " + id + " deleted!");
    }


}
