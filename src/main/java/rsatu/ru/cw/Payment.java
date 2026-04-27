package rsatu.ru.cw;

import java.math.BigDecimal;
import java.util.Date;

class Payment {
    private int id;
    private int accountId;
    private BigDecimal amount;
    private Date date;

    public Payment(int id, int accountId, BigDecimal amount, Date date) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.date = date;
    }

    public BigDecimal getAmount() { return amount; }
}

