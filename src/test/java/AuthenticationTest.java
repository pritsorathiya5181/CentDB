import Authentication.RegisterUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

public class AuthenticationTest {
    @Test
    @Order(1)
    public void testRegistration() throws NoSuchAlgorithmException {
        RegisterUser registerUser = new RegisterUser();
        String email = "test@gmail.com";
        String username = "test";
        String password = "testPass";
        String securityQuestion = "In what city were you born?";
        String answer = "testCity";

        boolean status =  registerUser.doRegistration(email, username, password, securityQuestion, answer);
        Assertions.assertTrue(status);
    }
}
