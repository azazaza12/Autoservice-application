package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.nio.file.OpenOption;
import java.util.*;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

public class Login {

    private static final String USER_LOGIN_KEY = "login";
    private static final String USER_PASSWORD_KEY = "password";
    private static final String USER_ROLE_KEY = "role";

    @FXML
    private Button sayHelloButton;
    @FXML
    private Button sayGoodbyeButton;
    @FXML
    private TextField nameField;
    @FXML
    //private TextLabel textLabel;
    private Label textLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private RadioButton isMasterRadio;
    @FXML
    private RadioButton isOperatorRadio;

    private static HashMap<String, String> passAndRole = new HashMap<>();
    //private static Collection<String> passAndRole = new ArrayList<>() ;
    //private static HashMap<String,  HashMap<String, String> > users = new HashMap<>();
    private static Collection<String> users=new ArrayList<>(3);
    //private static HashMap<String, Collection<String> > users = new HashMap<>();
    //List<List<Double>> users = new ArrayList<>();

    public static void showTextWindow1(String text) {
        // Создаем новое окно с текстом
        Stage textWindow = new Stage();
        VBox vbox = new VBox();
        vbox.getChildren().add(new Label(text));
        textWindow.setScene(new Scene(vbox, 300, 100));
        textWindow.show();
    }


    public static void addUser(String login, String password, String role) {
        System.out.println("BBBBBBBBBBBBBBBBBBBBBBbbb");
        if(checkLogin(login,password)){
            System.out.println("CCCCCCCc");
            showTextWindow1("    Пользователь с такими данными уже существует.    ");
        }
        else{
            System.out.println("CCCCCCCc");
            users.add(login);
            users.add(password);
            users.add(role);
            writeToFile();
            users.clear();
            showTextWindow1("   Регистрация прошла успешно!   ");
        }
        /*try {
            if (!users.containsKey(login)){
                // Проверяем, есть ли уже пользователь с таким логином и паролем
                // Если пользователь не существует, добавляем его в HashMap
                passAndRole.put(password, role);
                //passAndRole.add(password);
                //passAndRole.add(role);
                users.put(login, passAndRole);
                writeToFile();

                showTextWindow("Регистрация прошла успешно!");

            }
        else {

                showTextWindow("Пользователь с такими данными уже существует.");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/


    // Остальной код класса...
    }

    public  static boolean checkLoginAndPassword(String login, String password) {
        /*try {
            if (!users.containsKey(login)) {
                // Проверяем, есть ли уже пользователь с таким логином и паролем
                // Если пользователь не существует, добавляем его в HashMap

                showTextWindow("Пользователь с такими данными уже существует.");
            } else {

                showTextWindow("Вход осуществлен");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        String filePath = "1.txt";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = bufferedReader.readLine();
            System.out.println(line);
            while (line  != null) {
                System.out.println(line);
                String[] words = line.split(" ");
                line = bufferedReader.readLine();
                //System.out.println(words[0]);
                //System.out.println(words[1]);
                if (words[0].equalsIgnoreCase(login)) {
                    System.out.println("First word of the line match.");
                    if (words[1].equalsIgnoreCase(password)) {
                        System.out.println("FIRST and SECOND word of the line match.");
                        return true;

                    }
                    else {
                        return false;
                    }
                } else {
                    System.out.println("First and second word of the line do not match.");
                    //return false;
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
        // Остальной код класса...
    }

    public static boolean checkLogin(String login, String password) {

        String filePath = "1.txt";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                System.out.println(words[0]);
                if (words[0].equalsIgnoreCase(login)) {
                    System.out.println(words[0]);
                    System.out.println("First word of the line match.");
                    return true;
                } else {
                    System.out.println("First and second word of the line DO NOT match.");
                }
            }
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public  static String getRole(String login) {
        String filePath = "1.txt";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = bufferedReader.readLine();
            System.out.println(line);
            while (line  != null) {
                System.out.println(line);
                String[] words = line.split(" ");
                line = bufferedReader.readLine();
                if (words[0].equalsIgnoreCase(login)) {
                    System.out.println("First word of the line match.");
                    return words[2];
                } else {
                    System.out.println("First and second word of the line do not match.");
                    //return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void writeToFile() {
        try{
        final String FILE_NAME = "1.txt";
        String input="";

        String output;
        //output=Files.readAllLines(Paths.get("1.txt")).get(0)+'\n';


        //String output=String.join("\n",users);
        output=String.join("\n",users);

        output = output.replaceAll("\n", " ");
        output+='\n';
        /*Files.writeString(Paths.get(FILE_NAME),output, StandardCharsets.UTF_8);*/


        /*Files.write(Paths.get(FILE_NAME),
                    Collections.singleton(output), StandardCharsets.UTF_8);*/



        FileWriter writer=new FileWriter(FILE_NAME,true);
        BufferedWriter bufferedWriter=new BufferedWriter(writer);
        bufferedWriter.write(output);
        bufferedWriter.close();


        /*Files.write(Paths.get(FILE_NAME),
                users.stream().toList(), StandardCharsets.UTF_8);

       /* Files.write(Paths.get(FILE_NAME),
                users.entrySet().stream().map(k->k.getKey()+"   "+k.getValue().entrySet().stream().map(v->v.getKey()+"  "+v.getValue()).toList()).collect(Collectors.toList()),
                StandardCharsets.UTF_8);*/


        boolean isFileExist = new File (FILE_NAME).exists();
        System.out.println("File with name '"+FILE_NAME+"' is exist : " + isFileExist);

        Files.lines(Paths.get(FILE_NAME), StandardCharsets.UTF_8).forEach(System.out::println);}
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
