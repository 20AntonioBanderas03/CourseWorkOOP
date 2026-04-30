package rsatu.ru.cw;

import java.math.BigDecimal;

public class PersonalAccount {
    private int apartmentId;
    private BigDecimal debt;

    public PersonalAccount(int apartmentId, BigDecimal debt) {
        this.apartmentId = apartmentId;
        this.debt = debt;
    }

    public int getApartmentId() { return apartmentId; }
    public void setApartmentId(int apartmentId) { this.apartmentId = apartmentId; }
    public BigDecimal getDebt() { return debt; }
    public void setDebt(BigDecimal debt) { this.debt = debt; }

    public void addCharge(BigDecimal amount) {
        debt = debt.add(amount);
    }

    public void addPayment(BigDecimal amount) {
        debt = debt.subtract(amount);
    }

    public boolean isOverpaid() {
        return debt.compareTo(BigDecimal.ZERO) < 0;
    }

    public BigDecimal getOverpayment() {
        if (isOverpaid()) {
            return debt.abs();
        }
        return BigDecimal.ZERO;
    }
}