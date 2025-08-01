package project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import static project.Config.*;



public class RegistrationApplication  {
    String login;
    String password;



    @FXML
    private TextField regLoginField;

    @FXML
    private PasswordField regPasswordField;

    @FXML
    private RadioButton isMasterRadio;

    @FXML
    private RadioButton isOperatorRadio;

    @FXML
    private RadioButton isAccountantRadio;

    @FXML
    private Button registrationButton;

    private boolean isMaster = true;
    private boolean isOperator = false;
    private boolean isAccountant = false;
    private String role="master";
    private static final String ALGORITHM = "SHA-256";
    @FXML
    private Label textLabel;

    @FXML
    public void initialize() {
        isMasterRadio.setOnAction(event ->  {
            isMaster = true;
            isOperator = false;
            isAccountant=false;
            isMasterRadio.setDisable(false);
            isOperatorRadio.setDisable(true);
            isAccountantRadio.setDisable(true);


        });

        isOperatorRadio.setOnAction(event ->  {
            isOperator=true;
            isMaster = false;
            isAccountant=false;
            isOperatorRadio.setDisable(false);
            isMasterRadio.setDisable(true);
            isAccountantRadio.setDisable(true);
        });

        isAccountantRadio.setOnAction(event ->  {
            isAccountant=true;
            isMaster = false;
            isOperator=false;
            isAccountantRadio.setDisable(false);
            isOperatorRadio.setDisable(true);
            isMasterRadio.setDisable(true);

        });
        registrationButton.setOnAction(event ->register());
    }


    private void register() {
        if(isMaster){
            role="master";
        }
        else if (isOperator){
            role="operator";
        }
        else{
            role="accountant";
        }

        login = regLoginField.getText();
        password = regPasswordField.getText();

        if (checkUserExists(login)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка регистрации");
            alert.setHeaderText(null);
            alert.setContentText("Пользователь с таким логином уже существует.");
            alert.showAndWait();
        } else {
            String hashedPassword = hashPassword(password);
            User user = new User(login, hashedPassword, role);
            saveUser(user);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Регистрация прошла успешно");
            alert.setHeaderText(null);
            alert.setContentText("Пользователь успешно зарегистрирован. Вы можете войти в систему теперь.");
            alert.showAndWait();
            registrationButton.getScene().getWindow().hide();


            try{
                Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
                Scene scene = new Scene(root, 500, 400);
                Stage stage = new Stage();
                stage.setTitle("Авторизация");
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean checkUserExists(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
            byte[] passwordBytes = password.getBytes();
            byte[] hashedPassword = messageDigest.digest(passwordBytes);
            return bytesToHex(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    public static void saveUser(User user) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (login, password, role) VALUES (?, ?, ?)")) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getRole());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public static boolean checkLoginCredentials(String username, String password) {

        try {
            //Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, hashPassword(password));
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUserRole(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement("SELECT role FROM users WHERE login = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, hashPassword(password));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("role");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}

