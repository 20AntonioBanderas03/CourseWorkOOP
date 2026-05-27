package rsatu.ru.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ResidentController {

    @FXML private Label apartmentLabel;
    @FXML private Label debtLabel;
    @FXML private TextField electricityReading;
    @FXML private TextField waterReading;
    @FXML private TextField paymentAmount;
    @FXML private TextArea receiptArea;

    private DataService dataService;
    private User currentUser;
    private AccountingService accountingService;
    private Apartment currentApartment;

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
        this.accountingService = new AccountingService(dataService);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadApartmentData();
    }

    private void loadApartmentData() {
        if (currentUser.getApartmentId() == null) return;
        Optional<Apartment> aptOpt = dataService.findApartmentByNumber(currentUser.getApartmentId());
        if (aptOpt.isPresent()) {
            currentApartment = aptOpt.get();
            apartmentLabel.setText("Квартира №" + currentApartment.getNumber() + " (" + currentApartment.getOwnerName() + ")");
            updateDebtDisplay();
        }
    }

    private void updateDebtDisplay() {
        BigDecimal debt = accountingService.getDebt(currentUser.getApartmentId());
        debtLabel.setText(ReportingService.getDebtString(debt));
    }

    @FXML
    private void handleSendReadings() {
        if (currentApartment == null) return;
        showAlert("Информация", "Показания отправлены администратору.\nОжидайте начисления.");
        electricityReading.clear();
        waterReading.clear();
    }

    @FXML
    private void handlePay() {
        try {
            BigDecimal amount = new BigDecimal(paymentAmount.getText());
            if (amount.compareTo(BigDecimal.ZERO) <= 0)
                throw new InvalidInputException("Сумма платежа должна быть положительной...");

            BigDecimal oldDebt = accountingService.getDebt(currentUser.getApartmentId());
            accountingService.applyPayment(currentUser.getApartmentId(), amount);
            BigDecimal newDebt = accountingService.getDebt(currentUser.getApartmentId());

            updateDebtDisplay();
            receiptArea.setText(String.format("Платёж на %.2f руб принят!\nБыло: %s\nСтало: %s",
                    amount, ReportingService.getDebtString(oldDebt), ReportingService.getDebtString(newDebt)));
            paymentAmount.clear();

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректную сумму платежа!");
        } catch (InvalidInputException e){
            showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML
    private void handleShowReceipt() {
        if (currentApartment == null) return;
        BigDecimal currentDebt = accountingService.getDebt(currentUser.getApartmentId());
        receiptArea.setText(String.format("""
                ═══════════════════════════════════════
                      ТЕКУЩЕЕ СОСТОЯНИЕ
                ═══════════════════════════════════════
                Квартира №%d (%s)
                %s
                ═══════════════════════════════════════
                """, currentApartment.getNumber(), currentApartment.getOwnerName(), ReportingService.getDebtString(currentDebt)));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleAboutAuthor(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Об авторе");
        alert.setHeaderText("Карпушев Антон Андреевич");
        alert.setContentText("Группа: ИПБ-24\nДата рождения: 24.10.2003");
        alert.showAndWait();
    }

    @FXML
    public void handleAboutAccount(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Об аккаунте");
        alert.setHeaderText(String.format("id: %d\nlogin: %s", currentUser.getId(), currentUser.getLogin()));
        alert.setContentText(String.format("OwnerName: %s\nArea: %f\nResidentsCount: %d\nNumber: %d ", currentApartment.getOwnerName(), currentApartment.getArea(), currentApartment.getResidentsCount(), currentApartment.getNumber()));
        alert.showAndWait();
    }

    @FXML
    public void handleExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    public void handleShowReceiptPopup(ActionEvent actionEvent) {
        Popup infoPopup = new Popup();
        infoPopup.setAutoHide(true);
        infoPopup.setHideOnEscape(true);

        TextArea textContent = new TextArea();
        textContent.setEditable(false);
        textContent.setWrapText(true);
        textContent.setStyle("""
        -fx-font-family: 'Courier New', monospace;
        -fx-font-size: 12px;
        -fx-background-color: white;
        -fx-control-inner-background: white;
        """);

        BigDecimal currentDebt = accountingService.getDebt(currentUser.getApartmentId());

        String formattedText = String.format("""
        ═══════════════════════════════════════
              ТЕКУЩЕЕ СОСТОЯНИЕ
        ═══════════════════════════════════════
        Квартира №%d (%s)
        %s
        ═══════════════════════════════════════
        """,
                currentApartment.getNumber(),
                currentApartment.getOwnerName(),
                ReportingService.getDebtString(currentDebt)
        );

        textContent.setText(formattedText);
        textContent.setPrefSize(300, 150);

        Button closeButton = new Button("Закрыть");
        closeButton.setStyle("""
        -fx-background-color: #d32f2f;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-padding: 5 15;
        """);
        closeButton.setOnAction(e -> infoPopup.hide());

        VBox content = new VBox(10);
        content.setStyle("""
        -fx-background-color: white;
        -fx-border-color: #d32f2f;
        -fx-border-width: 2;
        -fx-border-radius: 1;
        -fx-padding: 10;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);
        """);

        content.getChildren().addAll(textContent, closeButton);
        infoPopup.getContent().add(content);

        javafx.stage.Window window = ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        javafx.scene.Node sourceNode = (javafx.scene.Node) actionEvent.getSource();
        double x = sourceNode.localToScreen(sourceNode.getBoundsInLocal()).getMinX();
        double y = sourceNode.localToScreen(sourceNode.getBoundsInLocal()).getMaxY();

        infoPopup.show(window, x, y);
    }
}