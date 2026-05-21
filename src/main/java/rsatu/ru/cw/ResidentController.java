package rsatu.ru.cw;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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
                throw new InvalidInputException("Ошибка! Сумма платежа должна быть положительной...");

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
            showAlert("Ошибка", "Сумма платежа должна быть положительной!");
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
}