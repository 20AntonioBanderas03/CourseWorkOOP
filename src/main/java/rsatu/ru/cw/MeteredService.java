package rsatu.ru.cw;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MeteredService extends Service {
    private double currentReading;
    private double previousReading;

    public MeteredService(String name, BigDecimal tariff, String unit, double currentReading, double previousReading) {
        super(name, tariff, unit);
        this.currentReading = currentReading;
        this.previousReading = previousReading;
    }

    @Override
    public BigDecimal calculate() {
        double diff = currentReading - previousReading;
        if (diff < 0) diff = 0;
        return tariff.multiply(BigDecimal.valueOf(diff)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculate(Meter meter) {
        double diff = meter.getDifference();
        if (diff < 0) diff = 0;
        return tariff.multiply(BigDecimal.valueOf(diff)).setScale(2, RoundingMode.HALF_UP);
    }

    public void setCurrentReading(double currentReading) { this.currentReading = currentReading; }
    public double getCurrentReading() { return currentReading; }
    public double getPreviousReading() { return previousReading; }
}