package Authentication;

import Constants.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class RegisterUser {

    public UserModel RegisteringUser = new UserModel();
    ArrayList<String> questions = new ArrayList<String>() {
        {
            add("In what city were you born?");
            add("What is the name of your favorite pet?");
            add("What is your mother's maiden name?");
            add("What high school did you attend?");
            add("What is your mother's maiden name?");
            add("What is the name of your first school?");
            add("What was the make of your first car?");
            add("What was your favorite food as a child?");
            add("Where did you meet your spouse?");
        }
    };

    public boolean register() throws NoSuchAlgorithmException {

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter credential for user");
        System.out.println("Enter email");
        String email = sc.next();
        RegisteringUser.setEmail(email);

        System.out.println("Enter username");
        String username = sc.next();
        RegisteringUser.setUsername(username);

        System.out.println("Enter password");
        String password = sc.next();


        System.out.println("Confirm password");
        String confirmPassword = sc.next();
        if(!password.equals(confirmPassword)){
            System.out.println("both the password should be same, please re enter the form.");
            return false;
        }

        System.out.println("Choose any one from given Security questions");
        for (int i =0;i<questions.size();i++){
           String ques = String.format("%d-> %s",i+1,questions.get(i));
           System.out.println( ques);
        }

        int securityQuestionOption = sc.nextInt();
        String securityQuestion =questions.get(securityQuestionOption-1);
        RegisteringUser.setSecurityQuestion(securityQuestion);

        System.out.println("set a Security Answer");
        String answer = sc.next();
        RegisteringUser.setSecurityAnswer(answer);


        if (username == null || password == null) {
            System.out.println("Please re-enter username and password.");
            return false;
        }

        return doRegistration(email, username, password, securityQuestion, answer);
    }


    public boolean doRegistration(String email, String username, String password, String securityQuestion, String answer) throws NoSuchAlgorithmException {
        File myFile = new File(fileLocation.USER_CREDENTIAL_PATH);
        final String hashedPassword = HashAlgorithm.toHexString(HashAlgorithm.getSHA(password));
        RegisteringUser.setPassword(hashedPassword);
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

            String toBeWritten = String.format("%s;%s;%s;%s;%s\n", username, email, securityQuestion, answer,hashedPassword);
            fw.write(toBeWritten);
            fw.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
