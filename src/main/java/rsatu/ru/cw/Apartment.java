package rsatu.ru.cw;

public class Apartment {
    private int number;
    private double area;
    private int residentsCount;
    private String ownerName;

    public Apartment(int number, double area, int residentsCount, String ownerName) {
        this.number = number;
        this.area = area;
        this.residentsCount = residentsCount;
        this.ownerName = ownerName;
    }

    // Геттеры и сеттеры
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }
    public int getResidentsCount() { return residentsCount; }
    public void setResidentsCount(int residentsCount) { this.residentsCount = residentsCount; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}