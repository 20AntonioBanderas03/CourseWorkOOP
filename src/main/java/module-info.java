module rsatu.ru.cw {
    requires javafx.controls;
    requires javafx.fxml;


    opens rsatu.ru.cw to javafx.fxml;
    exports rsatu.ru.cw;
}