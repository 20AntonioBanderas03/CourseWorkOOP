package rsatu.ru.cw;

import java.math.BigDecimal;

public abstract class Service implements Chargeable {
    protected String name;
    protected BigDecimal tariff;
    protected String unit;

    public Service(String name, BigDecimal tariff, String unit) {
        this.name = name;
        this.tariff = tariff;
        this.unit = unit;
    }

    public String getName() { return name; }
    public BigDecimal getTariff() { return tariff; }
    public String getUnit() { return unit; }
}