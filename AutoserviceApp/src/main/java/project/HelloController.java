package project;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import static project.Config.*;
import static project.RegistrationApplication.checkLoginCredentials;
import static project.RegistrationApplication.getUserRole;



public class HelloController {
    String login;
    String password;

    @FXML
    private Button loginButton;

    @FXML
    private Button noAccountButton;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;


    @FXML
    public void initialize() {
        //initializeConfig();
        loginButton.setOnAction(event -> handleLogin());
        noAccountButton.setOnAction(event -> handleNoAccount());
    }


    public static void showTextWindow(String text) {
        Stage textWindow = new Stage();
        VBox vbox = new VBox();
        vbox.getChildren().add(new Label(text));
        textWindow.setScene(new Scene(vbox, 300, 100));
        textWindow.show();
    }

    public void handleLogin() {
        login = loginField.getText();
        password = passwordField.getText();

        if(checkLoginCredentials(login, password)){
            if(getUserRole(login, password).equals("operator")) {
                starts(login);
            }
            else if(getUserRole(login, password).equals("accountant")){
                startsForAccountant(login);
            }
            else {
                startsForMaster(login);
            }
        }
        else {
            showTextWindow("    Пароль или логин неверный!!  ");
        }

    }
    public void handleNoAccount()  {

        noAccountButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("registration.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 500, 400));
        stage.show();

    }

    public void starts(String login) {
        noAccountButton.getScene().getWindow().hide();
        Label loginLabel = new Label(" Оператор :   "  + login );
        loginLabel.setFont(loginLabel.getFont().font(30));
        loginLabel.setStyle("-fx-text-fill: white;");
        loginLabel.setStyle("-fx-background-color: orange;");

        Stage primaryStage = new Stage();
        Button openReferenceBooksButton = new Button("Справочники \n");
        openReferenceBooksButton.setOnAction(e -> openReferenceBooks());
        openReferenceBooksButton.setPrefSize(300, 120);


        Button openWorkButton = new Button("Таблица работ");
        openWorkButton.setOnAction(e -> printWorksWindow());
        openWorkButton.setPrefSize(500, 80);

        Button deleteWorkButton = new Button("Удалить работу");
        deleteWorkButton.setOnAction(e -> openDeleteWorkWindow());
        deleteWorkButton.setPrefSize(500, 80);

        Button assignWorkButton = new Button("Назначить работу");
        assignWorkButton.setOnAction(e -> assignWork());
        assignWorkButton.setPrefSize(500, 80);


        Button updateWorkButton = new Button("Изменить работу");
        updateWorkButton.setOnAction(e -> updateDateWorkOurWindow());
        updateWorkButton.setPrefSize(500, 80);


        Button totalCostButton = new Button("Общая стоимость работ");
        totalCostButton.setOnAction(e -> openTotalCostOfServiceWindow());
        totalCostButton.setPrefSize(400, 100);


        Button topMastersButton = new Button("Топ-5 мастеров месяца");
        topMastersButton.setOnAction(e -> topFiveMastersWindow());
        topMastersButton.setPrefSize(400, 100);



        Button openOtchetButton = new Button("Отчеты");
        openOtchetButton.setOnAction(e -> openOtchetWindow());
        openOtchetButton.setPrefSize(400, 100);

        Button exitButton = new Button("Выход");
        exitButton.setPrefSize(200, 70);
        exitButton.setStyle("-fx-background-color: red;");

        BackgroundImage myBI= new BackgroundImage(new Image("https://skorozvon.ru/images/2022/12/23/05_05_44_487_1_1.png",1000,1000,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);


        VBox root = new VBox(openReferenceBooksButton, openWorkButton, deleteWorkButton, assignWorkButton, updateWorkButton,  totalCostButton, topMastersButton, openOtchetButton, exitButton);

        Label lab1=new Label("\n \n \n  ");
        VBox vbox1 = new VBox(loginLabel, lab1);
        vbox1.setAlignment(Pos.TOP_LEFT);
        root.setAlignment(Pos.CENTER);
        vbox1.getChildren().add(root);
        vbox1.setBackground(new Background(myBI));

        Scene scene = new Scene(vbox1, 1300, 1000);
        primaryStage.setTitle("Управление базой данных");
        primaryStage.setScene(scene);
        exitButton.setOnAction(e -> openExitWindow(primaryStage));
        primaryStage.show();
    }


    //отчеты
    private void openOtchetWindow() {
        Stage TableStage = new Stage();

        Button xlsButton = new Button("Отчет в формате xls");
        xlsButton.setOnAction(event -> {
            try {
                exportTablesToExcel();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });

        Button exportButton = new Button("Отчет в формате txt");
        exportButton.setOnAction(event -> {
            try {
                Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                BufferedWriter writer = new BufferedWriter(new FileWriter("Отчет.txt"));
                Statement statement = connection.createStatement();
                writeToTextFile(writer, statement, "masters");
                writeToTextFile(writer, statement, "cars");
                writeToTextFile(writer, statement, "services");
                writeToTextFile(writer, statement, "works");
                writer.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        VBox root = new VBox(xlsButton, exportButton);
        Scene scene = new Scene(root, 500, 250);
        TableStage.setTitle("Отчетность");
        TableStage.setScene(scene);
        TableStage.show();
    }

    private void exportTablesToExcel() throws SQLException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                Workbook workbook = new XSSFWorkbook();
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                if (conn == null) {
                    throw new SQLException("Не удалось установить соединение с базой данных");
                }
                exportTableToExcel(conn, "masters", workbook);
                exportTableToExcel(conn, "cars", workbook);
                exportTableToExcel(conn, "services", workbook);
                exportTableToExcel(conn, "works", workbook);
                workbook.write(fos);
                workbook.close();
            }
        }
    }


    private void exportTableToExcel(Connection conn, String tableName, Workbook workbook) throws SQLException {
        Sheet sheet = workbook.createSheet(tableName);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + tableName);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();

        Row headerRow = sheet.createRow(0);
        for (int i = 1; i <= columnsNumber; i++) {
            Cell cell = headerRow.createCell(i - 1);
            cell.setCellValue(rsmd.getColumnName(i));
        }

        int rowIndex = 1;
        while (rs.next()) {
            Row dataRow = sheet.createRow(rowIndex);
            for (int i = 1; i <= columnsNumber; i++) {
                Cell cell = dataRow.createCell(i - 1);
                cell.setCellValue(rs.getString(i));
            }
            rowIndex++;
        }

        rs.close();
        stmt.close();
    }


    //формат txt
    private void writeToTextFile(BufferedWriter writer, Statement statement, String tableName) throws Exception {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
        writer.write("Таблица " + tableName + ":\n");

        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                writer.write(resultSet.getString(i) + "\t");
            }
            writer.write("\n");
        }
        writer.write("\n");
        resultSet.close();

    }


    public void openExitWindow(Stage  stage){
        stage.close();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("hello-view.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage1 = new Stage();
        stage1.setScene(new Scene(root, 500, 400));
        stage1.show();

    }



    public void startsForMaster(String login) {
        Label loginLabel = new Label(" Пользователь :   "  + login );
        noAccountButton.getScene().getWindow().hide();
        loginLabel.setFont(loginLabel.getFont().font(30));
        loginLabel.setStyle("-fx-text-fill: white;");
        loginLabel.setStyle("-fx-background-color: yellow;");

        Stage primaryStage = new Stage();

        Button openMasterButton = new Button("Таблица мастеров");
        openMasterButton.setOnAction(e -> printMasterWindow());
        openMasterButton.setPrefSize(400, 100);

        Button openCarButton = new Button("Таблица автомобилей");
        openCarButton.setOnAction(e -> printCarWindow());
        openCarButton.setPrefSize(400, 100);

        Button openServiceButton = new Button("Таблица услуг");
        openServiceButton.setOnAction(e -> printServiceWindow());
        openServiceButton.setPrefSize(400, 100);

        Button openWorkButton = new Button("Таблица работ");
        openWorkButton.setOnAction(e -> printWorksWindow());
        openWorkButton.setPrefSize(400, 100);

        Button exitButton = new Button("Выход");
        exitButton.setPrefSize(200, 70);
        exitButton.setStyle("-fx-background-color: red;");



        BackgroundImage myBI= new BackgroundImage(new Image("https://img.freepik.com/premium-vector/car-service-design_24911-27833.jpg?size=626&ext=jpg",1000,1000,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Label lab1=new Label("\n \n \n \n \n \n \n \n \n \n \n ");
        VBox root = new VBox(openMasterButton, openCarButton, openServiceButton, openWorkButton, exitButton);
        VBox vbox1 = new VBox(loginLabel, lab1);
        vbox1.setAlignment(Pos.TOP_LEFT);
        root.setAlignment(Pos.CENTER);
        vbox1.getChildren().add(root);
        vbox1.setBackground(new Background(myBI));

        Scene scene = new Scene(vbox1, 1300, 1000);

        primaryStage.setTitle("Управление базой данных");
        primaryStage.setScene(scene);
        exitButton.setOnAction(e -> openExitWindow(primaryStage));
        primaryStage.show();
    }


    public void startsForAccountant(String login){
        Label loginLabel = new Label(" Бухгалтер :   "  + login );
        loginLabel.setFont(loginLabel.getFont().font(30));
        loginLabel.setStyle("-fx-text-fill: white;");
        loginLabel.setStyle("-fx-background-color: green;");

        noAccountButton.getScene().getWindow().hide();
        Stage primaryStage = new Stage();

        Button openMasterButton = new Button("Таблица мастеров");
        openMasterButton.setOnAction(e -> printMasterWindow());
        openMasterButton.setPrefSize(400, 100);

        Button openCarButton = new Button("Таблица автомобилей");
        openCarButton.setOnAction(e -> printCarWindow());
        openCarButton.setPrefSize(400, 100);

        Button openServiceButton = new Button("Таблица услуг");
        openServiceButton.setOnAction(e -> printServiceWindow());
        openServiceButton.setPrefSize(400, 100);

        Button openWorkButton = new Button("Таблица работ");
        openWorkButton.setOnAction(e -> printWorksWindow());
        openWorkButton.setPrefSize(400, 100);

        Button openSalaryButton = new Button("Зарплата с премией");
        openSalaryButton.setOnAction(e -> openSalarywindow());
        openSalaryButton.setPrefSize(500, 80);

        Button exitButton = new Button("Выход");
        exitButton.setPrefSize(200, 70);
        exitButton.setStyle("-fx-background-color: red;");


        BackgroundImage myBI= new BackgroundImage(new Image("https://www.profguide.io/images/article/a/13/Ol9uicqr-h.jpg",1000,1000,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        VBox root = new VBox(openMasterButton, openCarButton, openServiceButton, openWorkButton, openSalaryButton, exitButton);
        Label lab1=new Label("\n \n \n \n \n \n \n \n \n \n \n ");
        VBox vbox1 = new VBox(loginLabel, lab1);
        vbox1.setAlignment(Pos.TOP_LEFT);

        root.setAlignment(Pos.CENTER);

        vbox1.getChildren().add(root);
        vbox1.setBackground(new Background(myBI));

        Scene scene = new Scene(vbox1, 1300, 1000);

        primaryStage.setTitle("Управление базой данных");
        primaryStage.setScene(scene);
        exitButton.setOnAction(e -> openExitWindow(primaryStage));
        primaryStage.show();
    }


    public void openReferenceBooks() {
        Stage TableStage = new Stage();
        Button openMastersTableButton = new Button("Таблица мастеров");
        openMastersTableButton.setOnAction(e -> openMasterTableWindow());

        Button openCarsTableButton = new Button("Таблица автомобилей ");
        openCarsTableButton.setOnAction(e -> openCarTableWindow());

        Button openServiceTableButton = new Button("Таблица услуг ");
        openServiceTableButton.setOnAction(e -> openServiceTableWindow());

        VBox root = new VBox(openMastersTableButton, openCarsTableButton, openServiceTableButton);
        Scene scene = new Scene(root, 300, 250);

        TableStage.setTitle("Справочники");
        TableStage.setScene(scene);
        TableStage.show();
    }


    private void openMasterTableWindow() {
        Stage TableStage = new Stage();

        Button printMasterButton = new Button("Посмотреть таблицу мастеров");
        printMasterButton.setOnAction(e -> printMasterWindow());


        Button updateMasterButton = new Button("Изменить таблицу мастеров");
        updateMasterButton.setOnAction(e -> openUpdateMastersWindow());

        Button addMasterButton = new Button("Добавить мастера");
        addMasterButton.setOnAction(e -> addMasterWindow());

        Button deleteMasterButton = new Button("Удалить мастера");
        deleteMasterButton.setOnAction(e -> {
            try {
                openDeleteMasterWindow();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        VBox root = new VBox( printMasterButton, updateMasterButton, addMasterButton, deleteMasterButton);
        Scene scene = new Scene(root, 500, 250);

        TableStage.setTitle("Таблица мастеров");
        TableStage.setScene(scene);
        TableStage.show();
    }

    private void printMasterWindow() {

        JFrame frame = new JFrame("Мастера");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);

        try  {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM masters ORDER BY master_id");

            JTable table = new JTable(buildTableModel(resultSet));
            JOptionPane.showMessageDialog(null, new JScrollPane(table), "Masters Table", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openUpdateMastersWindow() {
        Stage updatemastersStage = new Stage();

        Label idLabel1 = new Label("Id :");
        TextField idField1 = new TextField();

        Label nameLabel = new Label("Имя :");
        TextField nameField = new TextField();

        Button updateButton = new Button("Изменить");
        updateButton.setOnAction(e -> {
            int id1 = Integer.parseInt(idField1.getText());
            String name1 = nameField.getText();
            UpdateMastersFromTable(id1, name1);
            updatemastersStage.close();
        });

        VBox root = new VBox(idLabel1, idField1, nameLabel, nameField, updateButton);
        Scene scene = new Scene(root, 300, 250);

        updatemastersStage.setTitle("Изменить имя мастера");
        updatemastersStage.setScene(scene);
        updatemastersStage.show();
    }


    private void UpdateMastersFromTable(int id, String name){
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "update masters set name = '" + name + "' where master_id = " + id ;
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();

        int columnCount = metaData.getColumnCount();
        Vector<String> columnNames = new Vector<>();
        Vector<Vector<Object>> data = new Vector<>();

        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        while (resultSet.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(resultSet.getObject(i));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
    }

    private void addMasterWindow() {
        Stage addMasterStage = new Stage();
        Label idLabel = new Label("Id:");
        TextField idField = new TextField();
        Label masterNameLabel = new Label("Имя мастера");
        TextField masterNameField = new TextField();
        Button addButton = new Button("Добавить");

        addButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = masterNameField.getText();

                Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM masters");
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    int masterCount = Integer.parseInt(String.valueOf(count));

                    if(masterCount>10){
                        Stage stage = new Stage();

                        Label label1 = new Label("Мастеров уже больше 10!");
                        VBox root = new VBox(label1);
                        Scene scene = new Scene(root, 300, 100);

                        stage.setTitle("Ошибка!");
                        stage.setScene(scene);
                        stage.show();
                    }
                    else{
                        addMasterToTable(id, name);
                    }
                }

            }
            catch (Exception exception){
                exception.printStackTrace();
            }
            addMasterStage.close();
        });
        VBox root = new VBox(idLabel, idField, masterNameLabel, masterNameField, addButton);
        Scene scene = new Scene(root, 300, 250);
        addMasterStage.setTitle("Добавить мастера");
        addMasterStage.setScene(scene);
        addMasterStage.show();
    }


    private void addMasterToTable(int id, String masterName) {
        try {

            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO masters (master_id, name) VALUES (" + id + ", '" +  masterName + "')";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка выполнения процедуры: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }


    private void openDeleteMasterWindow() {
        Stage deleteMasterStage = new Stage();

        Label idLabel1 = new Label("Id мастера:");
        TextField idField1 = new TextField();

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(e -> {
            String id1 = idField1.getText();
            deleteMasterFromTable(id1);
            deleteMasterStage.close();
        });

        VBox root = new VBox(idLabel1, idField1, deleteButton);
        Scene scene = new Scene(root, 300, 250);
        deleteMasterStage.setTitle("Удалить мастера");
        deleteMasterStage.setScene(scene);
        deleteMasterStage.show();
    }


    private void deleteMasterFromTable(String id){
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM masters WHERE master_id = " + id ;
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //АВТОМОБИЛИ!!!!!

    private void openCarTableWindow() {
        Stage TableStage = new Stage();

        Button printCarButton = new Button("Посмотреть таблицу автомобилей");
        printCarButton.setOnAction(e -> printCarWindow());

        /*Button updateCarButton = new Button("Изменить таблицу автомобилей");
        updateCarButton.setOnAction(e -> openUpdateCarColorWindow());*/

        Button addCarButton = new Button("Добавить автомобиль");
        addCarButton.setOnAction(e -> addCarWindow());

        Button deleteCarButton = new Button("Удалить автомобиль");
        deleteCarButton.setOnAction(e -> {
            try {
                openDeleteCarWindow();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        //VBox root = new VBox( printCarButton, updateCarButton, addCarButton, deleteCarButton);
        VBox root = new VBox( printCarButton, addCarButton, deleteCarButton);
        Scene scene = new Scene(root, 500, 250);
        TableStage.setTitle("Таблица автомобилей");
        TableStage.setScene(scene);
        TableStage.show();
    }

    private void printCarWindow() {

        JFrame frame = new JFrame("Автомобили");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet carResultSet = statement.executeQuery("SELECT * FROM cars ORDER BY car_id");

            JTable table = new JTable(buildTableModel(carResultSet));
            JOptionPane.showMessageDialog(null, new JScrollPane(table), "Car Table", JOptionPane.INFORMATION_MESSAGE);

            carResultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void openUpdateCarColorWindow() {
        Stage updateCarStage = new Stage();

        Label idLabel1 = new Label("Id автомобиля:");
        TextField idField1 = new TextField();

        Label colorLabel = new Label("Цвет:");
        TextField colorField = new TextField();

        Button updateButton = new Button("Изменить");
        updateButton.setOnAction(e -> {
            int id1 = Integer.parseInt(idField1.getText());
            String color1 = colorField.getText();
            UpdateCarsColorFromTable(id1, color1);
            updateCarStage.close();
        });
        VBox root = new VBox(idLabel1, idField1, colorLabel, colorField, updateButton);
        Scene scene = new Scene(root, 300, 250);

        updateCarStage.setTitle("Изменить цвет");
        updateCarStage.setScene(scene);
        updateCarStage.show();
    }

    private void UpdateCarsColorFromTable(int id, String color){
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "update cars set color = '" + color + "' where car_id = " + id ;
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addCarWindow() {
        Stage addCarStage = new Stage();

        Label idLabel = new Label("Id:");
        TextField idField = new TextField();

        Label numLabel = new Label("Номер автомобиля:");
        TextField numField = new TextField();

        Label colorLabel = new Label("Цвет:");
        TextField colorField = new TextField();

        Label markLabel = new Label("Марка:");
        TextField markField = new TextField();

        Label isForeignLabel = new Label("Иностранный:");
        TextField isForeignField = new TextField();


        Button addButton = new Button("Добавить");
        addButton.setOnAction(e -> {
            int id = Integer.parseInt(idField.getText());
            String num = numField.getText();
            String color = colorField.getText();
            String mark = markField.getText();
            boolean isForeign = Boolean.parseBoolean(isForeignField.getText());


            addCarToTable(id, num, color, mark, isForeign);
            addCarStage.close();
        });

        VBox root = new VBox(idLabel, idField, numLabel, numField,  colorLabel, colorField, markLabel, markField, isForeignLabel, isForeignField, addButton);
        Scene scene = new Scene(root, 300, 250);
        addCarStage.setTitle("Добавить автомобиль");
        addCarStage.setScene(scene);
        addCarStage.show();
    }


    private void addCarToTable(int id, String num, String color, String mark, boolean isForeign) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO cars VALUES (" + id + ", '" +  num + "' , '" +  color + "' , '" +  mark + "' , '" +  isForeign + "')";
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка выполнения процедуры: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }


    private void openDeleteCarWindow() {
        Stage deleteCarStage = new Stage();

        Label idLabel1 = new Label("Id автомобиля:");
        TextField idField1 = new TextField();

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(e -> {
            String id1 = idField1.getText();
            deleteCarFromTable(id1);
            deleteCarStage.close();
        });

        VBox root = new VBox(idLabel1, idField1, deleteButton);
        Scene scene = new Scene(root, 300, 250);

        deleteCarStage.setTitle("Удалить мастера");
        deleteCarStage.setScene(scene);
        deleteCarStage.show();
    }


    private void deleteCarFromTable(String id){
        try {
            //Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM cars WHERE car_id = " + id ;
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //уСЛУГИ!!
    private void openServiceTableWindow() {
        Stage TableStage = new Stage();

        Button printServiceButton = new Button("Посмотреть таблицу услуг");
        printServiceButton.setOnAction(e -> printServiceWindow());

        Button updateCostOurButton = new Button("Изменить стоимость услуги для отечественного автомобиля");
        updateCostOurButton.setOnAction(e -> updateServiceCostOurWindow());

        Button updateCostForeignButton = new Button("Изменить стоимость услуги для зарубежного автомобиля");
        updateCostForeignButton.setOnAction(e -> updateServiceCostForeignWindow());


        Button addServiceButton = new Button("Добавить услугу");
        addServiceButton.setOnAction(e -> addServiceWindow());

        Button deleteServiceButton = new Button("Удалить услугу");
        deleteServiceButton.setOnAction(e -> {
            try {
                openDeleteServiceWindow();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        VBox root = new VBox( printServiceButton, updateCostOurButton, updateCostForeignButton, addServiceButton, deleteServiceButton);
        Scene scene = new Scene(root, 500, 250);

        TableStage.setTitle("Таблица услуг");
        TableStage.setScene(scene);
        TableStage.show();

    }

    private void printServiceWindow() {

        JFrame frame = new JFrame("Услуги");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet carResultSet = statement.executeQuery("SELECT * FROM services ORDER BY service_id");

            JTable table = new JTable(buildTableModel(carResultSet));
            JOptionPane.showMessageDialog(null, new JScrollPane(table), "Service Table", JOptionPane.INFORMATION_MESSAGE);

            carResultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateServiceCostOurWindow() {
        Stage updateCarStage = new Stage();

        Label idLabel1 = new Label("Id услуги:");
        TextField idField1 = new TextField();

        Label costLabel = new Label("Стоимость для отечественного автомобиля:");
        TextField costField = new TextField();

        Button updateButton = new Button("Изменить");
        updateButton.setOnAction(e -> {
            int id1 = Integer.parseInt(idField1.getText());
            int cost = Integer.parseInt(costField.getText());
            UpdateServiceCostOurFromTable(id1, cost);
            updateCarStage.close();
        });

        VBox root = new VBox(idLabel1, idField1, costLabel, costField, updateButton);
        Scene scene = new Scene(root, 300, 250);

        updateCarStage.setTitle("Изменить стоимость");
        updateCarStage.setScene(scene);
        updateCarStage.show();
    }

    private void UpdateServiceCostOurFromTable(int id, int cost){
        try {

            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "update services set cost_our = '" + cost + "' where service_id = " + id ;
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateServiceCostForeignWindow() {
        Stage updateCarStage = new Stage();

        Label idLabel1 = new Label("Id услуги:");
        TextField idField1 = new TextField();

        Label costLabel = new Label("Стоимость для зарубежного автомобиля:");
        TextField costField = new TextField();

        Button updateButton = new Button("Изменить");
        updateButton.setOnAction(e -> {
            int id1 = Integer.parseInt(idField1.getText());
            int cost = Integer.parseInt(costField.getText());
            UpdateServiceCostForeignFromTable(id1, cost);
            updateCarStage.close();
        });

        VBox root = new VBox(idLabel1, idField1, costLabel, costField, updateButton);
        Scene scene = new Scene(root, 300, 250);

        updateCarStage.setTitle("Изменить стоимость");
        updateCarStage.setScene(scene);
        updateCarStage.show();
    }

    private void UpdateServiceCostForeignFromTable(int id, int cost){
        try {
            //Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "update services set cost_foreign = '" + cost + "' where service_id = " + id ;
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addServiceWindow() {
        Stage addServiceStage = new Stage();

        Label idLabel = new Label("Id:");
        TextField idField = new TextField();

        Label serviceNameLabel = new Label("Наименование:");
        TextField serviceNameField = new TextField();

        Label costOurLabel = new Label("Стоимость для отчественного автомобиля:");
        TextField costOurField = new TextField();

        Label costForeignLabel = new Label("Стоимость для зарубежного автомобиля:");
        TextField costForeignField = new TextField();

        Button addButton = new Button("Добавить");
        addButton.setOnAction(e -> {
            int id = Integer.parseInt(idField.getText());
            String name = serviceNameField.getText();
            int costOur = Integer.parseInt(costOurField.getText());
            int costForeign = Integer.parseInt(costForeignField.getText());
            addServiceToTable(id, name, costOur, costForeign);
            addServiceStage.close();
        });

        VBox root = new VBox(idLabel, idField, serviceNameLabel, serviceNameField,  costOurLabel, costOurField, costForeignLabel, costForeignField, addButton);
        Scene scene = new Scene(root, 300, 250);
        addServiceStage.setTitle("Добавить услугу");
        addServiceStage.setScene(scene);
        addServiceStage.show();
    }


    private void addServiceToTable(int id, String name, int costOur, int costForeign) {
        try {
            //Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO services VALUES (" + id + ", '" +  name + "' , '" +  costOur + "' , '" +  costForeign + "')";
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openDeleteServiceWindow() {
        Stage deleteCarStage = new Stage();

        Label idLabel1 = new Label("Id услуги:");
        TextField idField1 = new TextField();

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(e -> {
            String id1 = idField1.getText();
            deleteServiceFromTable(id1);
            deleteCarStage.close();
        });

        VBox root = new VBox(idLabel1, idField1, deleteButton);
        Scene scene = new Scene(root, 300, 250);

        deleteCarStage.setTitle("Удалить услугу");
        deleteCarStage.setScene(scene);
        deleteCarStage.show();
    }


    private void deleteServiceFromTable(String id){
        try {
            //Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM services WHERE service_id = " + id ;
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void assignWork() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);

            Statement masterStatement = connection.createStatement();
            ResultSet masterResultSet = masterStatement.executeQuery("SELECT * FROM masters ORDER BY master_id");
            JTable masterTable = new JTable(buildTableModel(masterResultSet));

            Statement serviceStatement = connection.createStatement();
            ResultSet serviceResultSet = serviceStatement.executeQuery("SELECT * FROM services ORDER BY service_id");
            JTable serviceTable = new JTable(buildTableModel(serviceResultSet));

            Statement carStatement = connection.createStatement();
            ResultSet carResultSet = carStatement.executeQuery("SELECT * FROM cars ORDER BY car_id");
            JTable carTable = new JTable(buildTableModel(carResultSet));

            Statement workStatement = connection.createStatement();
            ResultSet workResultSet = workStatement.executeQuery("SELECT * FROM works ORDER BY work_id");
            JTable workTable = new JTable(buildTableModel(workResultSet));

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Masters", new JScrollPane(masterTable));
            tabbedPane.addTab("Services", new JScrollPane(serviceTable));
            tabbedPane.addTab("Cars", new JScrollPane(carTable));
            tabbedPane.addTab("Works", new JScrollPane(workTable));

            JFrame frame = new JFrame("Database Tables");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(tabbedPane);
            frame.pack();

            JTextField jobNumberField = new JTextField(10);
            JTextField dateField = new JTextField(10);
            JTextField masterField = new JTextField(10);
            JTextField carField = new JTextField(10);
            JTextField serviceField = new JTextField(10);


            JPanel inputPanel = new JPanel();
            inputPanel.add(new JLabel("Id:"));
            inputPanel.add(jobNumberField);
            inputPanel.add(new JLabel("Дата:"));
            inputPanel.add(dateField);
            inputPanel.add(new JLabel("Id мастера:"));
            inputPanel.add(masterField);

            inputPanel.add(new JLabel("Id автомобиля:"));
            inputPanel.add(carField);

            inputPanel.add(new JLabel("Id услуги:"));
            inputPanel.add(serviceField);
            JButton addButton = new JButton("Add");
            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Integer jobNumber = Integer.parseInt(jobNumberField.getText());
                    //Timestamp date = Timestamp.valueOf(dateField.getText());
                    LocalDate date= LocalDate.parse(dateField.getText());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String formattedDate = date.format(formatter);
                    System.out.println(formattedDate);
                    //Date date= Date(dateField.getText());
                    int master = Integer.parseInt(masterField.getText());
                    int car = Integer.parseInt(carField.getText());
                    int service = Integer.parseInt(serviceField.getText());

                    try {
                        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                        Statement stmt = conn.createStatement();
                        String sql = "INSERT INTO works VALUES (" + jobNumber + ", '" + formattedDate + "' , " + master + " , " + car + ", " + service + ")";
                        stmt.executeUpdate(sql);

                        stmt.close();
                    }
                    catch (Exception exception){
                        if(exception.getMessage().equals("ОШИБКА: Мастер уже выполнил более одной работы в этот день.\n" +
                                "  Где: функция PL/pgSQL warn_master_work_count(), строка 5, оператор RAISE")){
                            //showTextWindow("Неверный регион автомобиля!");
                            Stage stage = new Stage();

                            Label label1 = new Label("        ОШИБКА: Мастер уже выполнил более одной работы в этот день!           ");
                            VBox root = new VBox(label1);
                            Scene scene = new Scene(root, 300, 100);

                            stage.setTitle("Ошибка!");
                            stage.setScene(scene);
                            stage.show();
                        }
                        exception.printStackTrace();
                    }
                }
            });


            inputPanel.add(addButton);
            frame.add(inputPanel, BorderLayout.SOUTH);
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setVisible(true);


            masterResultSet.close();
            masterStatement.close();
            serviceResultSet.close();
            serviceStatement.close();
            carResultSet.close();
            carStatement.close();
            connection.close();
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    frame.setVisible(false);
                    frame.dispose();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void printWorksWindow() {

        JFrame frame = new JFrame("Работы");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);


        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM works ORDER BY work_id");

            JTable table = new JTable(buildTableModel(resultSet));
            JOptionPane.showMessageDialog(null, new JScrollPane(table), "Works Table", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateDateWorkOurWindow() {
        Stage updateStage = new Stage();

        Label idLabel1 = new Label("Id работы:");
        TextField idField1 = new TextField();

        Label dateLabel = new Label("Дата:");
        TextField dateField = new TextField();

        Button updateButton = new Button("Изменить");
        updateButton.setOnAction(e -> {
            int id1 = Integer.parseInt(idField1.getText());
            LocalDate date= LocalDate.parse(dateField.getText());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);
            UpdateDateWorkFromTable(id1, formattedDate);
            updateStage.close();
        });
        VBox root = new VBox(idLabel1, idField1, dateLabel, dateField, updateButton);
        Scene scene = new Scene(root, 300, 250);

        updateStage.setTitle("Изменить дату");
        updateStage.setScene(scene);
        updateStage.show();
    }

    private void UpdateDateWorkFromTable(int id, String date){
        try {
            //Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "update works set date_work = '" + date + "' where work_id = " + id ;
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().equals("ОШИБКА: Нельзя изменить дату работы более чем на один день!\n" +
                    "  Где: функция PL/pgSQL warn_invalid_date(), строка 5, оператор RAISE")){
                //showTextWindow("Неверный регион автомобиля!");
                Stage stage = new Stage();

                Label label1 = new Label("        Нельзя изменить дату работы более чем на один день!           ");
                VBox root = new VBox(label1);
                Scene scene = new Scene(root, 300, 100);

                stage.setTitle("Ошибка!");
                stage.setScene(scene);
                stage.show();

            }

        }
    }

    private void openDeleteWorkWindow() {
        Stage deleteWorkStage = new Stage();

        Label idLabel1 = new Label("Id работы:");
        TextField idField1 = new TextField();

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(e -> {
            String id1 = idField1.getText();
            deleteWorkFromTable(id1);
            deleteWorkStage.close();
        });

        VBox root = new VBox(idLabel1, idField1, deleteButton);
        Scene scene = new Scene(root, 300, 250);

        deleteWorkStage.setTitle("Удалить работу");
        deleteWorkStage.setScene(scene);
        deleteWorkStage.show();
    }


    private void deleteWorkFromTable(String id){
        try {
            //Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM works WHERE work_id = " + id ;
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openTotalCostOfServiceWindow() {
        Stage stage = new Stage();

        Label startDateLabel = new Label("Дата начала:");
        TextField startDateField = new TextField();

        Label endDateLabel = new Label("Дата конца:");
        TextField endDateField = new TextField();

        Button getCostButton = new Button("Вывести общую стоимость обслуживания");
        getCostButton.setOnAction(e -> {
            LocalDate date1= LocalDate.parse(startDateField.getText());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate1 = date1.format(formatter);
            LocalDate date2= LocalDate.parse(endDateField.getText());
            String formattedDate2 = date2.format(formatter);

            getTotalCostOfService(formattedDate1, formattedDate2);
            stage.close();
        });

        VBox root = new VBox(startDateLabel, startDateField, endDateLabel, endDateField, getCostButton);
        Scene scene = new Scene(root, 300, 250);
        stage.setTitle("Общая стоимость обслуживания");
        stage.setScene(scene);
        stage.show();
    }


    private void getTotalCostOfService(String formattedDate1, String formattedDate2){
        Stage stage = new Stage();

        String text1 = "Дата начала:" + formattedDate1;
        String text2 = "Дата конца:" + formattedDate2;
        Label startDateLabel = new Label(text1);

        Label endDateLabel = new Label(text2);

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String totalCostQuery = "SELECT SUM(CASE WHEN cars.is_foreign = false THEN services.cost_our ELSE services.cost_foreign END) AS total_cost " +
                    "FROM works " +
                    "JOIN cars ON works.fk_car_id = cars.car_id " +
                    "JOIN services ON works.fk_service_id = services.service_id " +
                    "WHERE works.date_work >= '" + formattedDate1 + "' AND works.date_work <= '" + formattedDate2 + "'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(totalCostQuery);
            if (resultSet.next()) {
                double cost = resultSet.getDouble(1);
                double totalCost = Double.parseDouble(String.valueOf(cost));
                Label costLabel = new Label("Общая стоимость обслуживания: " + totalCost);
                VBox root = new VBox(startDateLabel, endDateLabel, costLabel );
                Scene scene = new Scene(root, 300, 250);


                stage.setTitle("Общая стоимость обслуживания");
                stage.setScene(scene);
                stage.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void topFiveMastersWindow() {
        Stage stage = new Stage();

        Label monthLabel = new Label("Номер месяца: ");
        TextField monthField = new TextField();

        Label yearLabel = new Label("Год: ");
        TextField yearField = new TextField();


        Button getCostButton = new Button("Вывести 5 мастеров, которые в заданном месяце \n выполнили наибольшее число работ для разных автомобилей");
        getCostButton.setOnAction(e -> {
            int month = Integer.parseInt(monthField.getText());

            int year = Integer.parseInt(yearField.getText());

            getTopFiveMasters(month, year);
            stage.close();
        });

        VBox root = new VBox(monthLabel, monthField, yearLabel,  yearField, getCostButton);
        Scene scene = new Scene(root, 300, 250);


        stage.setTitle("Лучшие мастера");
        stage.setScene(scene);
        stage.show();
    }


    private void getTopFiveMasters(int month, int year){
        String date1;
        String date2;
        if (month==12){
            date1 = String.valueOf(year) + "-" + String.valueOf(month) + "-01";
            date2 = String.valueOf(year+1) + "-01-01";
        }
        else{
            date1 = String.valueOf(year) + "-" + String.valueOf(month) + "-01";
            date2 = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-01";
        }
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            String topMastersQuery = "SELECT masters.master_id, masters.name, COUNT(DISTINCT works.fk_car_id) AS num_cars_serviced " +
                    "FROM works " +
                    "JOIN masters ON works.fk_master_id = masters.master_id " +
                    "WHERE works.date_work >='" + date1 + "' AND works.date_work < '" + date2 + "'" +
                    "GROUP BY masters.master_id " +
                    "ORDER BY num_cars_serviced DESC " +
                    "LIMIT 5";
            Statement masterStatement = connection.createStatement();
            ResultSet masterResultSet = masterStatement.executeQuery(topMastersQuery);
            JTable topMastersTable = new JTable(buildTableModel(masterResultSet));

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Мастера", new JScrollPane(topMastersTable));

            JFrame frame = new JFrame("Лучшие 5 мастеров");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(tabbedPane);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setVisible(true);

            masterResultSet.close();
            masterStatement.close();

            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    frame.setVisible(false);
                    frame.dispose();
                }
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }



    }


    private void openSalarywindow(){
        Stage stage = new Stage();

        Label idLabel = new Label("Id мастера: ");
        TextField idField = new TextField();

        Label salaryLabel = new Label("Зарплата: ");
        TextField salaryField = new TextField();
        Button getSalaryButton = new Button("Посчитать");
        getSalaryButton.setOnAction(e -> {
            int id = Integer.parseInt(idField.getText());

            double salary = Double.parseDouble(salaryField.getText());

            getSalaryWithBonus(id, salary);
            stage.close();
        });

        VBox root = new VBox(idLabel, idField, salaryLabel,  salaryField, getSalaryButton);
        Scene scene = new Scene(root, 300, 250);


        stage.setTitle("Зарплата с учетом премии");
        stage.setScene(scene);
        stage.show();
    }

    public double getSalaryWithBonus(int id, double salary) {

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            String storedProcedureName = "get_salary_with_bonus";

            Stage salaryStage = new Stage();

            ResultSet resultSet = statement.executeQuery("CALL " + storedProcedureName + "(" + id + "," + salary + "," + null +")");
            if (resultSet.next()) {
                double sal = resultSet.getDouble(1);
                System.out.println(sal);
                double salaryWithBonus = Double.parseDouble(String.valueOf(sal));
                System.out.println(salaryWithBonus);


                Label idLabel1 = new Label("Id мастера:" + id);
                Label salaryLabel1 = new Label("Зарплата с учетом премии:" + salaryWithBonus);


                VBox root = new VBox(idLabel1,salaryLabel1);
                Scene scene = new Scene(root, 300, 250);

                salaryStage.setTitle("Зарплата");
                salaryStage.setScene(scene);
                salaryStage.show();
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}



