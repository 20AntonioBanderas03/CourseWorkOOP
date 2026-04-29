package rsatu.ru.cw;

public class Meter<T extends Number> {
    private int id;
    private T previousReading;
    private T currentReading;
    private String unit;

    public Meter(int id, T previousReading, T currentReading, String unit) {
        this.id = id;
        this.previousReading = previousReading;
        this.currentReading = currentReading;
        this.unit = unit;
    }

    public double getDifference() {
        return currentReading.doubleValue() - previousReading.doubleValue();
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public T getCurrentReading() { return currentReading; }
    public void setCurrentReading(T currentReading) { this.currentReading = currentReading; }
}