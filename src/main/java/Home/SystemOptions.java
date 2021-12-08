package Home;

import Authentication.*;
import Query.*;

import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class SystemOptions {

    public static void main(String[] args) throws NoSuchAlgorithmException, FileNotFoundException {
        UserModel activeUser = null;
        System.out.println("Welcome!");
        while (true) {
            System.out.println("1. Register User");
            System.out.println("2. Login User");
            System.out.println("3. Exit");
            System.out.println("Select an option from above");
            Scanner sc = new Scanner(System.in);
            int option = sc.nextInt();

            switch (option) {
                case 1 -> {
                    RegisterUser registerUser = new RegisterUser();
                    boolean register = registerUser.register();
                    if (register) {
                        activeUser = registerUser.RegisteringUser;
                        System.out.println("Registered Successfully");
                    } else {
                        System.out.println("Please enter valid user credential");
                    }
                }
                case 2 -> {
                    LoginUser loginUser = new LoginUser();
                    boolean login = loginUser.login();
                    if (login) {
                        System.out.println("User logged in");
                        activeUser = loginUser.LoginUser;
                        QueryOptions options = new QueryOptions(activeUser);
                        options.listQueryOptions();
                    } else {
                        System.out.println("Please enter valid username or password");
                    }
                }
                case 3 -> System.exit(0);
            }
        }
    }
}
