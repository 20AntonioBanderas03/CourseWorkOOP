package rsatu.ru.cw;

class Apartment {
    private int number;
    private double area;
    private int personalAccountId;

    public Apartment(int number, double area, int personalAccountId) {
        this.number = number;
        this.area = area;
        this.personalAccountId = personalAccountId;
    }

    public int getNumber() { return number; }
    public double getArea() { return area; }
    public int getPersonalAccountId() { return personalAccountId; }
}
