package rsatu.ru.cw;


public interface Chargeable<T> {
    T calculate(Meter meter);
}
