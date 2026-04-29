package rsatu.ru.cw;

import java.math.BigDecimal;
import java.util.List;

public class AccountingService {
    private final DataService dataService;

    public AccountingService(DataService dataService) {
        this.dataService = dataService;
    }

    // Начисление для конкретной квартиры по услугам
    public BigDecimal calculateForApartment(int apartmentId, List<Service> services) {
        BigDecimal total = BigDecimal.ZERO;
        for (Service s : services) {
            total = total.add(s.calculate());
        }
        return total;
    }

    // Применить начисление к долгу квартиры
    public void applyCharge(int apartmentId, BigDecimal amount) {
        PersonalAccount account = dataService.findAccountByApartmentId(apartmentId)
                .orElse(new PersonalAccount(apartmentId, BigDecimal.ZERO));
        account.addCharge(amount);
        dataService.saveAccount(account);
    }

    // Применить платёж от жильца
    public void applyPayment(int apartmentId, BigDecimal amount) {
        PersonalAccount account = dataService.findAccountByApartmentId(apartmentId)
                .orElse(new PersonalAccount(apartmentId, BigDecimal.ZERO));
        account.addPayment(amount);
        dataService.saveAccount(account);
    }

    // Получить текущий долг квартиры
    public BigDecimal getDebt(int apartmentId) {
        return dataService.findAccountByApartmentId(apartmentId)
                .map(PersonalAccount::getDebt)
                .orElse(BigDecimal.ZERO);
    }
}