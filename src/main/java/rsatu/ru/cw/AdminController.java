package rsatu.ru.cw;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AdminController {

    @FXML private TableView<ApartmentTableRow> apartmentTable;
    @FXML private TableColumn<ApartmentTableRow, Integer> colNumber;
    @FXML private TableColumn<ApartmentTableRow, String> colOwner;
    @FXML private TableColumn<ApartmentTableRow, String> colDebt;
    @FXML private TableColumn<ApartmentTableRow, Void> colAction;

    @FXML private TextField electricityReading;
    @FXML private TextField waterReading;
    @FXML private Label apartmentInfoLabel;
    @FXML private TextArea receiptArea;

    private DataService dataService;
    private User currentUser;
    private AccountingService accountingService;
    private ObservableList<ApartmentTableRow> tableData = FXCollections.observableArrayList();
    private int selectedApartmentNumber = -1;

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
        this.accountingService = new AccountingService(dataService);
        loadApartments();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        colNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        colDebt.setCellValueFactory(new PropertyValueFactory<>("debtString"));
        apartmentTable.setItems(tableData);

        apartmentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedApartmentNumber = selected.getNumber();
                apartmentInfoLabel.setText("Выбрана квартира №" + selectedApartmentNumber + " (" + selected.getOwnerName() + ")");
                electricityReading.clear();
                waterReading.clear();
            }
        });
    }

    private void loadApartments() {
        tableData.clear();
        List<Apartment> apartments = dataService.getApartments();
        for (Apartment apt : apartments) {
            BigDecimal debt = accountingService.getDebt(apt.getNumber());
            String debtString = ReportingService.getDebtString(debt);
            ApartmentTableRow row = new ApartmentTableRow(apt.getNumber(), apt.getOwnerName(), debtString);
            tableData.add(row);
        }
    }

    @FXML
    private void handleChargeSelected() {
        if (selectedApartmentNumber == -1) {
            showAlert("Ошибка", "Выберите квартиру в таблице!");
            return;
        }

        try {
            double electricity = electricityReading.getText().isEmpty() ? 0 : Double.parseDouble(electricityReading.getText());
            double water = waterReading.getText().isEmpty() ? 0 : Double.parseDouble(waterReading.getText());

            Optional<Apartment> aptOpt = dataService.findApartmentByNumber(selectedApartmentNumber);
            if (aptOpt.isEmpty()) return;
            Apartment apt = aptOpt.get();

            MeteredService electricService = new MeteredService("Электричество", BigDecimal.valueOf(5.80), "кВт*ч", electricity, 0);
            MeteredService waterService = new MeteredService("Холодная вода", BigDecimal.valueOf(45.00), "м³", water, 0);
            NormativeService heatingService = new NormativeService("Отопление", BigDecimal.valueOf(25.50), "кв.м", 0, apt.getArea());
            NormativeService sewerService = new NormativeService("Водоотведение", BigDecimal.valueOf(120.00), "чел", apt.getResidentsCount(), 0);

            List<Service> services = List.of(heatingService, sewerService, electricService, waterService);

            BigDecimal total = accountingService.calculateForApartment(selectedApartmentNumber, services);
            BigDecimal oldDebt = accountingService.getDebt(selectedApartmentNumber);
            accountingService.applyCharge(selectedApartmentNumber, total);
            BigDecimal newDebt = accountingService.getDebt(selectedApartmentNumber);

            String receipt = ReportingService.generateReceipt(selectedApartmentNumber, apt.getOwnerName(), services, oldDebt, newDebt);
            receiptArea.setText(receipt);

            showAlert("Успех", String.format("Начислено %.2f руб для квартиры №%d", total, selectedApartmentNumber));
            loadApartments();

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректные показания счётчиков!");
        }
    }

    @FXML
    private void handleRefresh() {
        loadApartments();
        receiptArea.clear();
        apartmentInfoLabel.setText("Выберите квартиру из списка");
        selectedApartmentNumber = -1;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class ApartmentTableRow {
        private final int number;
        private final String ownerName;
        private final String debtString;

        public ApartmentTableRow(int number, String ownerName, String debtString) {
            this.number = number;
            this.ownerName = ownerName;
            this.debtString = debtString;
        }
        public int getNumber() { return number; }
        public String getOwnerName() { return ownerName; }
        public String getDebtString() { return debtString; }
    }

    @FXML
    private void handleManageApartments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rsatu/ru/cw/manage-apartments.fxml"));
            Scene scene = new Scene(loader.load(), 700, 600);
            ManageApartmentsController controller = loader.getController();
            controller.setDataService(dataService);

            Stage stage = new Stage();
            stage.setTitle("Управление квартирами");
            stage.setScene(scene);
            stage.showAndWait();

            loadApartments();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно управления квартирами: " + e.getMessage());
        }
    }
}