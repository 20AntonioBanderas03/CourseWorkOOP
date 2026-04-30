package rsatu.ru.cw;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.Optional;

public class ManageApartmentsController {

    @FXML private TableView<ApartmentTableRow> apartmentTable;
    @FXML private TableColumn<ApartmentTableRow, Integer> colNumber;
    @FXML private TableColumn<ApartmentTableRow, Double> colArea;
    @FXML private TableColumn<ApartmentTableRow, Integer> colResidents;
    @FXML private TableColumn<ApartmentTableRow, String> colOwner;
    @FXML private TableColumn<ApartmentTableRow, String> colDebt;

    @FXML private TextField numberField;
    @FXML private TextField areaField;
    @FXML private TextField residentsField;
    @FXML private TextField ownerField;
    @FXML private Label statusLabel;

    private DataService dataService;
    private AccountingService accountingService;
    private ObservableList<ApartmentTableRow> tableData = FXCollections.observableArrayList();
    private ApartmentTableRow selectedApartment;

    @FXML
    public void initialize() {
        colNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        colResidents.setCellValueFactory(new PropertyValueFactory<>("residentsCount"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        colDebt.setCellValueFactory(new PropertyValueFactory<>("debtString"));

        apartmentTable.setItems(tableData);

        apartmentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedApartment = selected;
                fillFieldsFromSelected();
            }
        });
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
        this.accountingService = new AccountingService(dataService);
        loadApartments();
    }

    private void loadApartments() {
        tableData.clear();
        for (Apartment apt : dataService.getApartments()) {
            BigDecimal debt = accountingService.getDebt(apt.getNumber());
            String debtString = ReportingService.getDebtString(debt);
            tableData.add(new ApartmentTableRow(
                    apt.getNumber(),
                    apt.getArea(),
                    apt.getResidentsCount(),
                    apt.getOwnerName(),
                    debtString
            ));
        }
    }

    private void fillFieldsFromSelected() {
        if (selectedApartment != null) {
            numberField.setText(String.valueOf(selectedApartment.getNumber()));
            areaField.setText(String.valueOf(selectedApartment.getArea()));
            residentsField.setText(String.valueOf(selectedApartment.getResidentsCount()));
            ownerField.setText(selectedApartment.getOwnerName());
        }
    }

    private void clearFields() {
        numberField.clear();
        areaField.clear();
        residentsField.clear();
        ownerField.clear();
        selectedApartment = null;
        apartmentTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh() {
        loadApartments();
        clearFields();
        statusLabel.setText("Список обновлён");
    }

    @FXML
    private void handleAddApartment() {
        clearFields();
        statusLabel.setText("Введите данные новой квартиры и нажмите 'Сохранить'");
        numberField.requestFocus();
    }

    @FXML
    private void handleEditApartment() {
        if (selectedApartment == null) {
            showAlert("Ошибка", "Выберите квартиру для редактирования!");
            return;
        }
        statusLabel.setText("Редактирование квартиры №" + selectedApartment.getNumber());
    }

    @FXML
    private void handleDeleteApartment() {
        if (selectedApartment == null) {
            showAlert("Ошибка", "Выберите квартиру для удаления!");
            return;
        }

        BigDecimal debt = accountingService.getDebt(selectedApartment.getNumber());
        if (debt.compareTo(BigDecimal.ZERO) != 0) {
            showAlert("Ошибка", "Нельзя удалить квартиру с долгом или переплатой!\n" +
                    "Текущий баланс: " + ReportingService.getDebtString(debt));
            return;
        }

        boolean hasUser = dataService.getUsers().stream()
                .anyMatch(u -> u.getApartmentId() != null && u.getApartmentId() == selectedApartment.getNumber());
        if (hasUser) {
            showAlert("Ошибка", "Нельзя удалить квартиру с зарегистрированным жильцом!\n" +
                    "Сначала удалите пользователя из системы.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение удаления");
        confirm.setHeaderText(null);
        confirm.setContentText("Удалить квартиру №" + selectedApartment.getNumber() + "?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            dataService.deleteApartment(selectedApartment.getNumber());
            dataService.deleteAccount(selectedApartment.getNumber());
            loadApartments();
            clearFields();
            statusLabel.setText("Квартира №" + selectedApartment.getNumber() + " удалена");
        }
    }

    @FXML
    private void handleSave() {
        try {
            String numberStr = numberField.getText();
            String areaStr = areaField.getText();
            String residentsStr = residentsField.getText();
            String ownerName = ownerField.getText();

            if (numberStr.isEmpty() || areaStr.isEmpty() || residentsStr.isEmpty() || ownerName.isEmpty()) {
                showAlert("Ошибка", "Заполните все поля!");
                return;
            }

            int number = Integer.parseInt(numberStr);
            double area = Double.parseDouble(areaStr);
            int residents = Integer.parseInt(residentsStr);

            if (area <= 0 || residents <= 0) {
                showAlert("Ошибка", "Площадь и количество жильцов должны быть положительными!");
                return;
            }

            Optional<Apartment> existingApt = dataService.findApartmentByNumber(number);

            if (selectedApartment == null) {
                if (existingApt.isPresent()) {
                    showAlert("Ошибка", "Квартира с номером " + number + " уже существует!");
                    return;
                }

                Apartment newApartment = new Apartment(number, area, residents, ownerName);
                dataService.saveApartment(newApartment);

                dataService.saveAccount(new PersonalAccount(number, BigDecimal.ZERO));

                statusLabel.setText("Квартира №" + number + " добавлена!");
                loadApartments();
                clearFields();
            } else {
                if (existingApt.isPresent() && existingApt.get().getNumber() != selectedApartment.getNumber()) {
                    showAlert("Ошибка", "Квартира с номером " + number + " уже существует!");
                    return;
                }

                dataService.updateApartment(number, area, residents, ownerName);
                statusLabel.setText("Квартира №" + number + " обновлена!");
                loadApartments();
                clearFields();
            }

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Проверьте правильность ввода чисел!\n" +
                    "Площадь и количество жильцов должны быть числами.");
        } catch (Exception e) {
            showAlert("Ошибка", "Произошла ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
        statusLabel.setText("Поля очищены");
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
        private final double area;
        private final int residentsCount;
        private final String ownerName;
        private final String debtString;

        public ApartmentTableRow(int number, double area, int residentsCount, String ownerName, String debtString) {
            this.number = number;
            this.area = area;
            this.residentsCount = residentsCount;
            this.ownerName = ownerName;
            this.debtString = debtString;
        }

        public int getNumber() { return number; }
        public double getArea() { return area; }
        public int getResidentsCount() { return residentsCount; }
        public String getOwnerName() { return ownerName; }
        public String getDebtString() { return debtString; }
    }
}