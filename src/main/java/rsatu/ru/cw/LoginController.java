package rsatu.ru.cw;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private TextField regLoginField;
    @FXML private PasswordField regPasswordField;
    @FXML private TextField regApartmentField;
    @FXML private TabPane tabPane;

    private DataService dataService;
    private AuthService authService;

    @FXML
    public void initialize() {
        dataService = new DataService();
        authService = new AuthService(dataService);
    }

    @FXML
    private void handleLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Заполните все поля!");
            return;
        }

        Optional<User> userOpt = authService.login(login, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            openMainWindow(user);
        } else {
            showAlert("Ошибка", "Неверный логин или пароль!");
        }
    }

    @FXML
    private void handleRegister() {
        String login = regLoginField.getText();
        String password = regPasswordField.getText();
        String apartmentStr = regApartmentField.getText();

        if (login.isEmpty() || password.isEmpty() || apartmentStr.isEmpty()) {
            showAlert("Ошибка", "Заполните все поля!");
            return;
        }

        int apartmentNumber;
        try {
            apartmentNumber = Integer.parseInt(apartmentStr);
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Номер квартиры должен быть числом!");
            return;
        }

        boolean success = authService.register(login, password, apartmentNumber);
        if (success) {
            showAlert("Успех", "Регистрация прошла успешно! Теперь войдите.");
            tabPane.getSelectionModel().select(0);
            loginField.clear();
            passwordField.clear();
            regLoginField.clear();
            regPasswordField.clear();
            regApartmentField.clear();
        } else {
            showAlert("Ошибка", "Не удалось зарегистрироваться. Проверьте:\n- Логин свободен\n- Квартира существует\n- На квартиру ещё нет жильца");
        }
    }

    private void openMainWindow(User user) {
        try {
            Stage stage = (Stage) loginField.getScene().getWindow();
            FXMLLoader loader;

            if ("ADMIN".equals(user.getRole())) {
                loader = new FXMLLoader(getClass().getResource("/rsatu/ru/cw/admin.fxml"));
                Scene scene = new Scene(loader.load(), 700, 500);
                AdminController controller = loader.getController();
                controller.setDataService(dataService);
                controller.setCurrentUser(user);
                stage.setTitle("Панель администратора");
                stage.setScene(scene);
            } else {
                loader = new FXMLLoader(getClass().getResource("/rsatu/ru/cw/resident.fxml"));
                Scene scene = new Scene(loader.load(), 600, 550);
                ResidentController controller = loader.getController();
                controller.setDataService(dataService);
                controller.setCurrentUser(user);
                stage.setTitle("Личный кабинет жильца");
                stage.setScene(scene);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить интерфейс: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}