package rsatu.ru.cw;

import java.math.BigDecimal;

class NormativeService extends Service {
    private int personsCount;
    private double area;

    public NormativeService(String name, BigDecimal tariff, String unit, int personsCount, double area) {
        super(name, tariff, unit);
        this.personsCount = personsCount;
        this.area = area;
    }

    @Override
    public BigDecimal calculate() {
        if ("чел".equals(unit)) {
            return tariff.multiply(BigDecimal.valueOf(personsCount));
        }
        else if ("кв.м".equals(unit)) {
            return tariff.multiply(BigDecimal.valueOf(area));
        }
        return BigDecimal.ZERO;
    }
}
