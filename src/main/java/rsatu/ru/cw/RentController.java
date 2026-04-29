package rsatu.ru.cw;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public class RentController {

    @FXML private TextField apartmentNumberField;
    @FXML private TextArea receiptArea;
    @FXML private Label debtLabel;
    @FXML private TextField paymentAmountField;
    @FXML private TextField electricityReadingField;
    @FXML private TextField waterReadingField;

    private DataService dataService;
    private AccountingService accountingService;
    private AuthService authService;
    private User currentUser;
    private Apartment currentApartment;

    @FXML
    public void initialize() {
        dataService = new DataService();
        accountingService = new AccountingService(dataService);
        authService = new AuthService(dataService);

        // Для теста - создаём тестовую квартиру, если её нет
        if (dataService.findApartmentByNumber(1).isEmpty()) {
            dataService.saveApartment(new Apartment(1, 45.5, 3, "Иванов И.И."));
        }

        // Пробуем найти пользователя или создаём тестового
        Optional<User> userOpt = authService.login("test", "test");
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
        } else {
            authService.register("test", "test", 1);
            currentUser = authService.login("test", "test").orElse(null);
        }

        if (currentUser != null && currentUser.getApartmentId() != null) {
            Optional<Apartment> aptOpt = dataService.findApartmentByNumber(currentUser.getApartmentId());
            currentApartment = aptOpt.orElse(null);
            apartmentNumberField.setText(String.valueOf(currentUser.getApartmentId()));
        }

        updateDebtDisplay();
    }

    @FXML
    private void handleCalculate() {
        if (currentApartment == null) {
            receiptArea.setText("Ошибка: квартира не найдена!");
            return;
        }

        try {
            double electricity = electricityReadingField.getText().isEmpty() ? 0 : Double.parseDouble(electricityReadingField.getText());
            double water = waterReadingField.getText().isEmpty() ? 0 : Double.parseDouble(waterReadingField.getText());

            MeteredService electricService = new MeteredService("Электричество", BigDecimal.valueOf(5.80), "кВт*ч", electricity, 0);
            MeteredService waterService = new MeteredService("Холодная вода", BigDecimal.valueOf(45.00), "м³", water, 0);
            NormativeService heatingService = new NormativeService("Отопление", BigDecimal.valueOf(25.50), "кв.м", 0, currentApartment.getArea());
            NormativeService sewerService = new NormativeService("Водоотведение", BigDecimal.valueOf(120.00), "чел", currentApartment.getResidentsCount(), 0);

            List<Service> services = List.of(heatingService, sewerService, electricService, waterService);

            BigDecimal total = BigDecimal.ZERO;
            for (Service s : services) {
                total = total.add(s.calculate());
            }

            BigDecimal oldDebt = accountingService.getDebt(currentApartment.getNumber());
            accountingService.applyCharge(currentApartment.getNumber(), total);
            BigDecimal newDebt = accountingService.getDebt(currentApartment.getNumber());

            String receipt = ReportingService.generateReceipt(
                    currentApartment.getNumber(),
                    currentApartment.getOwnerName(),
                    services,
                    oldDebt,
                    newDebt
            );
            receiptArea.setText(receipt);
            updateDebtDisplay();

        } catch (NumberFormatException e) {
            receiptArea.setText("Ошибка: введите корректные показания счётчиков");
        }
    }

    @FXML
    private void handlePay() {
        if (currentApartment == null) {
            receiptArea.setText("Ошибка: квартира не найдена!");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(paymentAmountField.getText());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                receiptArea.setText("Ошибка: сумма платежа должна быть положительной");
                return;
            }

            BigDecimal oldDebt = accountingService.getDebt(currentApartment.getNumber());
            accountingService.applyPayment(currentApartment.getNumber(), amount);
            BigDecimal newDebt = accountingService.getDebt(currentApartment.getNumber());

            updateDebtDisplay();
            receiptArea.setText(String.format("Платёж на %.2f руб принят!\nБыло: %s\nСтало: %s",
                    amount, ReportingService.getDebtString(oldDebt), ReportingService.getDebtString(newDebt)));
            paymentAmountField.clear();

        } catch (NumberFormatException e) {
            receiptArea.setText("Ошибка: введите корректную сумму платежа");
        }
    }

    @FXML
    private void handleShowDebt() {
        if (currentApartment == null) {
            receiptArea.setText("Ошибка: квартира не найдена!");
            return;
        }
        BigDecimal debt = accountingService.getDebt(currentApartment.getNumber());
        receiptArea.setText(ReportingService.getDebtString(debt));
        updateDebtDisplay();
    }

    private void updateDebtDisplay() {
        if (currentApartment != null) {
            BigDecimal debt = accountingService.getDebt(currentApartment.getNumber());
            debtLabel.setText(ReportingService.getDebtString(debt));
        }
    }
}