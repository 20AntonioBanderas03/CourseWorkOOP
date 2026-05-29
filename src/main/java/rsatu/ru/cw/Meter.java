package rsatu.ru.cw;

public class Meter<T extends Number> {
    private T previousReading;
    private T currentReading;

    public Meter(T previousReading, T currentReading) {
        this.previousReading = previousReading;
        this.currentReading = currentReading;
    }

    public double getDifference() {
        return currentReading.doubleValue() - previousReading.doubleValue();
    }

    public T getPreviousReading() {
        return previousReading;
    }

    public void setPreviousReading(T previousReading) {
        this.previousReading = previousReading;
    }

    public T getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(T currentReading) {
        this.currentReading = currentReading;
    }
}