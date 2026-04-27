package rsatu.ru.cw;

public abstract class Person {
    protected String fullName;
    protected String phone;

    public Person(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }
}
