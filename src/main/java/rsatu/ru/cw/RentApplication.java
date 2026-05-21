package rsatu.ru.cw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RentApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/rsatu/ru/cw/login.fxml"));
        Scene scene = new Scene(loader.load(), 500, 550);
        primaryStage.setTitle("Система расчёта квартплаты - Вход");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}