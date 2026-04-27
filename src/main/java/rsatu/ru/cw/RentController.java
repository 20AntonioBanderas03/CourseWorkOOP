package rsatu.ru.cw;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class RentController {

    @FXML private TextField apartmentNumberField;
    @FXML private TextArea receiptArea;
    @FXML private Label debtLabel;
    @FXML private TextField paymentAmountField;
    @FXML private TextField electricityReadingField;
    @FXML private TextField waterReadingField;

    private PersonalAccount currentAccount;
    private AccountingService accountingService;
    private MeteredService electricityService;
    private MeteredService waterService;

    @FXML
    public void initialize() {
        currentAccount = new PersonalAccount(1001);

        electricityService = new MeteredService("Электричество",
                BigDecimal.valueOf(5.80), "кВт*ч", 350.0, 280.0);
        waterService = new MeteredService("Холодная вода",
                BigDecimal.valueOf(45.00), "м³", 12.5, 9.2);

        List<Service> services = List.of(
                new NormativeService("Отопление", BigDecimal.valueOf(25.50), "кв.м", 0, 45.5),
                new NormativeService("Водоотведение", BigDecimal.valueOf(120.00), "чел", 3, 0),
                electricityService,
                waterService
        );

        accountingService = new AccountingService(currentAccount, services);
        updateDebtDisplay();
    }

    @FXML
    private void handleCalculate() {
        try {
            if (!electricityReadingField.getText().isEmpty()) {
                double newReading = Double.parseDouble(electricityReadingField.getText());
                electricityService.setCurrentReading(newReading);
            }
            if (!waterReadingField.getText().isEmpty()) {
                double newReading = Double.parseDouble(waterReadingField.getText());
                waterService.setCurrentReading(newReading);
            }
        } catch (NumberFormatException e) {
            receiptArea.setText("Ошибка: введите корректные показания счётчиков");
            return;
        }

        accountingService.applyCharges();
        updateDebtDisplay();

        String receipt = ReportingService.generateReceipt(
                currentAccount,
                accountingService.getServices(),
                YearMonth.now()
        );
        receiptArea.setText(receipt);
    }

    @FXML
    private void handlePay() {
        try {
            BigDecimal amount = new BigDecimal(paymentAmountField.getText());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                receiptArea.setText("Ошибка: сумма платежа должна быть положительной");
                return;
            }
            accountingService.applyPayment(amount);
            updateDebtDisplay();
            receiptArea.setText("Платёж принят!\n" +
                    ReportingService.generateShortReport(currentAccount));
            paymentAmountField.clear();
        } catch (NumberFormatException e) {
            receiptArea.setText("Ошибка: введите корректную сумму платежа");
        }
    }

    @FXML
    private void handleShowDebt() {
        receiptArea.setText(ReportingService.generateShortReport(currentAccount));
        updateDebtDisplay();
    }

    private void updateDebtDisplay() {
        debtLabel.setText(String.format("Долг: %.2f руб", currentAccount.getDebt()));
    }
}