package rsatu.ru.cw;

import java.math.BigDecimal;
import java.util.List;

public class AccountingService {
    private final DataService dataService;

    public AccountingService(DataService dataService) {
        this.dataService = dataService;
    }

    public BigDecimal calculateForApartment(int apartmentId, List<ServiceMeterPairs> pairs) {
        BigDecimal total = BigDecimal.ZERO;
        for (ServiceMeterPairs p : pairs) {
            total = total.add((BigDecimal) p.service().calculate(p.meter()));
        }
        return total;
    }

    public void applyCharge(int apartmentId, BigDecimal amount) {
        PersonalAccount account = dataService.findAccountByApartmentId(apartmentId)
                .orElse(new PersonalAccount(apartmentId, BigDecimal.ZERO));
        account.addCharge(amount);
        dataService.saveAccount(account);
    }

    public void applyPayment(int apartmentId, BigDecimal amount) {
        PersonalAccount account = dataService.findAccountByApartmentId(apartmentId)
                .orElse(new PersonalAccount(apartmentId, BigDecimal.ZERO));
        account.addPayment(amount);
        dataService.saveAccount(account);
    }

    public BigDecimal getDebt(int apartmentId) {
        return dataService.findAccountByApartmentId(apartmentId)
                .map(PersonalAccount::getDebt)
                .orElse(BigDecimal.ZERO);
    }
}