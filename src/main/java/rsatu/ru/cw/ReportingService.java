package rsatu.ru.cw;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class ReportingService {

    public static String generateReceipt(int apartmentId, String ownerName, List<ServiceMeterPairs> pairs, BigDecimal debtBefore, BigDecimal debtAfter) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n════════════════════════════════\n");
        sb.append("         КВИТАНЦИЯ ОПЛАТЫ ЖКУ\n");
        sb.append("════════════════════════════════\n");
        sb.append("Период: ").append(YearMonth.now()).append("\n");
        sb.append("Квартира №").append(apartmentId).append("\n");
        sb.append("Собственник: ").append(ownerName).append("\n");
        sb.append("--------------------------------------------\n");
        sb.append("Услуга                     Сумма (руб)\n");
        sb.append("--------------------------------------------\n");

        BigDecimal total = BigDecimal.ZERO;
        for (ServiceMeterPairs p : pairs) {
            BigDecimal amount = (BigDecimal) p.service().calculate(p.meter());
            total = total.add(amount);
            sb.append(String.format("%-24s %10.2f\n", p.service().getName(), amount));
        }

        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-24s %10.2f\n", "ИТОГО К ОПЛАТЕ:", total));
        sb.append(String.format("%-24s %10.2f\n", "Долг ДО:", debtBefore));
        sb.append(String.format("%-24s %10.2f\n", "Долг ПОСЛЕ:", debtAfter));
        sb.append("════════════════════════════════\n");
        return sb.toString();
    }

    public static String getDebtString(BigDecimal debt) {
        if (debt.compareTo(BigDecimal.ZERO) < 0) {
            return String.format("Переплата: %.2f руб", debt.abs());
        } else if (debt.compareTo(BigDecimal.ZERO) > 0) {
            return String.format("Долг: %.2f руб", debt);
        } else {
            return "Задолженности нет";
        }
    }
}