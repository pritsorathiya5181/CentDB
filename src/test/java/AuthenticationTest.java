import Authentication.LoginUser;
import Authentication.RegisterUser;
import Constants.fileLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

public class AuthenticationTest {
    @Test
    @Order(1)
    public void testRegistration() throws NoSuchAlgorithmException {
        RegisterUser registerUser = new RegisterUser();
        Random rand = new Random();
        String email =   "test"+rand.nextInt(50)+"@gmail.com";
        String username = "test"+rand.nextInt(50);
        String password = "testPass";
        String securityQuestion = "In what city were you born?";
        String answer = "testCity";

        boolean status = registerUser.doRegistration(email, username, password, securityQuestion, answer);
        Assertions.assertTrue(status);
    }

    @Test
    @Order(2)
    public void testLogin() throws NoSuchAlgorithmException, FileNotFoundException {
        LoginUser loginUser = new LoginUser();
        String username = "test";
        String password = "testPass";
        String answer = "testCity";

        File myFile = new File(fileLocation.USER_CREDENTIAL_PATH);
        Scanner fileReader = new Scanner(myFile);
        boolean status = false;
        while (fileReader.hasNextLine()) {
            String data = fileReader.nextLine();
            String[] userMeta = data.split(";");

            if (username.equals(userMeta[0])) {
                status = loginUser.doLogin(password, userMeta, answer);
            }
        }

        Assertions.assertTrue(status);
    }
}
