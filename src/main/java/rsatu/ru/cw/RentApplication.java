package rsatu.ru.cw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RentApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("rent-view.fxml"));
        Scene scene = new Scene(loader.load(), 600, 800);
        primaryStage.setTitle("Система расчёта квартплаты");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

//TODO: Добавить шрифты из ресурсов
//TODO: Разобраться с программой
//TODO: Добавить привилегии для разных ролей
//TODO: Добавить добавить авторизацию юзеров
//TODO: Добавить хранение данных о юзерах в файле
// TODO: Добавить хравниние информации в лк пользователя
