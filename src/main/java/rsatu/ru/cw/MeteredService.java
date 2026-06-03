package rsatu.ru.cw;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MeteredService extends Service {
    public MeteredService(String name, BigDecimal tariff, String unit, double currentReading, double previousReading) {
        super(name, tariff, unit);
    }

    @Override
    public BigDecimal calculate(Meter meter) {
        double diff = meter.getDifference();
        if (diff < 0) diff = 0;
        return tariff.multiply(BigDecimal.valueOf(diff)).setScale(2, RoundingMode.HALF_UP);
    }
}