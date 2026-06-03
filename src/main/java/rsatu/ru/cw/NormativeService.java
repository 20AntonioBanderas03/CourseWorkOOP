package rsatu.ru.cw;

import java.math.BigDecimal;

public class NormativeService extends Service {
    public NormativeService(String name, BigDecimal tariff, String unit, int residentsCount, double area) {
        super(name, tariff, unit);
    }

    @Override
    public Object calculate(Meter meter) {
        if ("чел".equals(unit)) {
            return tariff.multiply(BigDecimal.valueOf((Double) meter.getPreviousReading()));
        } else if ("кв.м".equals(unit)) {
            return tariff.multiply(BigDecimal.valueOf((Double) meter.getCurrentReading()));
        }
        return BigDecimal.ZERO;
    }
}