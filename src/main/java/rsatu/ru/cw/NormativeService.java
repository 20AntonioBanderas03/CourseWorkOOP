package rsatu.ru.cw;

import java.math.BigDecimal;

public class NormativeService extends Service {
    private int residentsCount;
    private double area;

    public NormativeService(String name, BigDecimal tariff, String unit, int residentsCount, double area) {
        super(name, tariff, unit);
        this.residentsCount = residentsCount;
        this.area = area;
    }

    @Override
    public BigDecimal calculate() {
        if ("чел".equals(unit)) {
            return tariff.multiply(BigDecimal.valueOf(residentsCount));
        } else if ("кв.м".equals(unit)) {
            return tariff.multiply(BigDecimal.valueOf(area));
        }
        return BigDecimal.ZERO;
    }
}