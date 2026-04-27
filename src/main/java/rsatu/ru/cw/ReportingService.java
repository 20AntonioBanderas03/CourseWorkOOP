package rsatu.ru.cw;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class ReportingService {

    public static String generateReceipt(PersonalAccount account, List<Service> services, YearMonth period) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n════════════════════════════════\n");
        sb.append("         КВИТАНЦИЯ ОПЛАТЫ ЖКУ\n");
        sb.append("════════════════════════════════\n");
        sb.append("Период: ").append(period).append("\n");
        sb.append("Лицевой счет: №").append(account.getId()).append("\n");
        sb.append("--------------------------------------\n");
        sb.append("Услуга                     Сумма (руб)\n");
        sb.append("--------------------------------------\n");

        BigDecimal total = BigDecimal.ZERO;
        for (Service s : services) {
            BigDecimal amount = s.calculate();
            total = total.add(amount);
            sb.append(String.format("%-24s %10.2f\n", s.getName(), amount));
        }

        sb.append("--------------------------------------\n");
        sb.append(String.format("%-24s %10.2f\n", "ИТОГО:", total));
        sb.append(String.format("%-24s %10.2f\n", "Долг на начало:", account.getBalance().subtract(total)));
        sb.append(String.format("%-24s %10.2f\n", "Долг на конец:", account.getBalance()));
        sb.append("════════════════════════════════\n");
        return sb.toString();
    }

    public static String generateShortReport(PersonalAccount account) {
        return String.format("Лицевой счет №%d | Баланс: %.2f руб | Долг: %.2f руб",
                account.getId(), account.getBalance(), account.getDebt());
    }
}