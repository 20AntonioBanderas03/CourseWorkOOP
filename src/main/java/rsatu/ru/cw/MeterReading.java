package rsatu.ru.cw;

import java.util.Date;

class MeterReading {
    private int meterId;
    private double value;
    private Date date;

    public MeterReading(int meterId, double value, Date date) {
        this.meterId = meterId;
        this.value = value;
        this.date = date;
    }

    public double getValue() { return value; }
}
