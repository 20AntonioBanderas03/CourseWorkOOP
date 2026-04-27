package rsatu.ru.cw;

import java.math.BigDecimal;
import java.time.YearMonth;

class Charge {
    private int id;
    private int accountId;
    private String serviceName;
    private BigDecimal amount;
    private YearMonth period;

    public Charge(int id, int accountId, String serviceName, BigDecimal amount, YearMonth period) {
        this.id = id;
        this.accountId = accountId;
        this.serviceName = serviceName;
        this.amount = amount;
        this.period = period;
    }

    public BigDecimal getAmount() { return amount; }
    public String getServiceName() { return serviceName; }
}
