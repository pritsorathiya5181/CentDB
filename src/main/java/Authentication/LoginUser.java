package Authentication;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import static Constants.fileLocation.USER_CREDENTIAL_PATH;

public class LoginUser {
    public boolean login() throws FileNotFoundException, NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter credential for user");
        System.out.println("Enter username");
        String username = sc.next();
        System.out.println("Enter password");
        String password = sc.next();

        File myFile = new File(USER_CREDENTIAL_PATH);
        Scanner fileReader = new Scanner(myFile);
        while (fileReader.hasNextLine()){
            String data = fileReader.nextLine();
            if(username.equals(data.split(";")[0])) {
                final String hashedPassword = HashAlgorithm.toHexString(HashAlgorithm.getSHA(password));
                return hashedPassword.equals(data.split(";")[1]);
            }
        }
        return false;
    }
}
