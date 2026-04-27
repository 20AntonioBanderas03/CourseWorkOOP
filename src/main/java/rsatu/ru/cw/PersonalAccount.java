package rsatu.ru.cw;

import java.math.BigDecimal;

public class PersonalAccount {
    private int id;
    private BigDecimal balance;
    private BigDecimal debt;

    public PersonalAccount(int id) {
        this.id = id;
        this.balance = BigDecimal.ZERO;
        this.debt = BigDecimal.ZERO;
    }

    public void addCharge(BigDecimal amount) {
        balance = balance.add(amount);
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            debt = balance;
        } else {
            debt = BigDecimal.ZERO;
        }
    }

    public void addPayment(BigDecimal amount) {
        balance = balance.subtract(amount);
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            debt = balance;
        } else {
            debt = BigDecimal.ZERO;
        }
    }

    public BigDecimal getBalance() { return balance; }
    public BigDecimal getDebt() { return debt; }
    public int getId() { return id; }
}