module rsatu.ru.cw {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens rsatu.ru.cw to com.google.gson, javafx.fxml;
    exports rsatu.ru.cw;
}