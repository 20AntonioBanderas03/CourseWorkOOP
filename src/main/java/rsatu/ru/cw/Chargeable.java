package rsatu.ru.cw;

import java.math.BigDecimal;

public interface Chargeable<T> {
    T calculate();
    T calculate(Meter meter);
}
