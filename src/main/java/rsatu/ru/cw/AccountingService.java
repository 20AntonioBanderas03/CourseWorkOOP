package rsatu.ru.cw;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class AccountingService {
    private PersonalAccount account;
    private List<Service> services;

    public AccountingService(PersonalAccount account, List<Service> services) {
        this.account = account;
        this.services = services;
    }

    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Service s : services) {
            BigDecimal amount = s.calculate();
            total = total.add(amount);
            System.out.printf("  - %s: %.2f руб%n", s.getName(), amount);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public void applyCharges() {
        BigDecimal total = calculateTotal();
        account.addCharge(total);
        System.out.printf("Итого начислено: %.2f руб%n", total);
    }

    public void applyPayment(BigDecimal amount) {
        account.addPayment(amount);
        System.out.printf("Принят платеж: %.2f руб. Текущий баланс: %.2f руб%n",
                amount, account.getBalance());
    }

    public void showDebt() {
        System.out.printf("Долг по лицевому счету №%d: %.2f руб%n",
                account.getId(), account.getDebt());
    }

    public PersonalAccount getAccount() { return account; }
    public List<Service> getServices() { return services; }
}