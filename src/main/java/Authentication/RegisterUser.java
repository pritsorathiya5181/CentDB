package Authentication;

import Constants.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class RegisterUser {
    public boolean register() throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter credential for user");
        System.out.println("Enter username");
        String username = sc.next();
        System.out.println("Enter password");
        String password = sc.next();

        if (username == null || password == null) {
            System.out.println("Please re-enter username and password.");
            return false;
        }

        File myFile = new File(fileLocation.USER_CREDENTIAL_PATH);
        final String hashedPassword = HashAlgorithm.toHexString(HashAlgorithm.getSHA(password));
        FileWriter fw;

        try {
            if (myFile.createNewFile()) {
                fw = new FileWriter(fileLocation.USER_CREDENTIAL_PATH);
            } else {
                Scanner fileReader = new Scanner(myFile);
                while (fileReader.hasNextLine()){
                    String data = fileReader.nextLine();
                    if(username.equals(data.split(";")[0])) {
                        System.out.println("User already registered");
                        return false;
                    }
                }

                fw = new FileWriter(fileLocation.USER_CREDENTIAL_PATH, true);
            }
            fw.write(username+";"+hashedPassword+"\n");
            fw.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
