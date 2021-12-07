package Authentication;

import Constants.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class LoginUser {
    public UserModel LoginUser = new UserModel();
    public boolean login() throws FileNotFoundException, NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter credential for user");
        System.out.println("Enter username");
        String username = sc.next();
        System.out.println("Enter password");
        String password = sc.next();



        File myFile = new File(fileLocation.USER_CREDENTIAL_PATH);
        Scanner fileReader = new Scanner(myFile);
        while (fileReader.hasNextLine()){
            String data = fileReader.nextLine();
            String [] userMeta = data.split(";");

            if(username.equals(userMeta[0])) {

                System.out.println("Answer your security question");
                System.out.println(userMeta[2]);
                String givenAnswer = sc.next();
                final String hashedPassword = HashAlgorithm.toHexString(HashAlgorithm.getSHA(password));
                final String rightAnswer = userMeta[3];

                if(hashedPassword.equals(userMeta[4]) && givenAnswer.equals(rightAnswer)){
                    LoginUser.setUsername(userMeta[0]);
                    LoginUser.setEmail(userMeta[1]);
                    LoginUser.setSecurityQuestion(userMeta[2]);
                    LoginUser.setSecurityAnswer(userMeta[3]);
                    LoginUser.setPassword(userMeta[4]);

                    return true;
                }

            }
        }

//        this.LoginUser = new UserModel(username,password,"email","dummy security","dummy answer");

        return false;
    }
}
