package rsatu.ru.cw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class DataService {
    private static final String DATA_FILE = "data.dll";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Map<String, Object> data = new HashMap<>();
    private int nextUserId = 1;
    private int nextApartmentId = 1;

    public DataService() {
        loadData();
        initDefaultAdmin();
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            //initEmptyData();
            initTestData();
            saveData();
            return;
        }
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            data = gson.fromJson(reader, type);
            if (data == null) initEmptyData();

            if (data.containsKey("nextUserId")) {
                nextUserId = ((Double) data.get("nextUserId")).intValue();
            }
            if (data.containsKey("nextApartmentId")) {
                nextApartmentId = ((Double) data.get("nextApartmentId")).intValue();
            }
        } catch (IOException e) {
            initEmptyData();
        }
    }

    private void initEmptyData() {
        data = new HashMap<>();
        data.put("users", new ArrayList<User>());
        data.put("apartments", new ArrayList<Apartment>());
        data.put("accounts", new ArrayList<PersonalAccount>());
        data.put("nextUserId", 1);
        data.put("nextApartmentId", 1);
    }

    private void initTestData() {
        List<Apartment> apartments = getApartments();
        if (apartments.isEmpty()) {
            saveApartment(new Apartment(1, 45.5, 3, "Иванов Иван Иванович"));
            saveApartment(new Apartment(2, 68.2, 2, "Петров Петр Петрович"));

            saveAccount(new PersonalAccount(1, java.math.BigDecimal.ZERO));
            saveAccount(new PersonalAccount(2, java.math.BigDecimal.ZERO));
        }
    }

    private void saveData() {
        data.put("nextUserId", nextUserId);
        data.put("nextApartmentId", nextApartmentId);
        try (Writer writer = new FileWriter(DATA_FILE)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDefaultAdmin() {
        List<User> users = getUsers();
        boolean adminExists = users.stream().anyMatch(u -> Roles.ADMIN.toString().equals(u.getRole()));
        if (!adminExists) {
            User admin = new User(nextUserId++, "admin", "admin", Roles.ADMIN.toString(), null);
            users.add(admin);
            saveUser(admin);
            saveData();
        }
    }

    public List<User> getUsers() {
        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        List<User> users = gson.fromJson(gson.toJson(data.get("users")), type);
        return users != null ? users : new ArrayList<>();
    }

    public void saveUser(User user) {
        List<User> users = getUsers();
        user.setId(nextUserId++);
        users.add(user);
        data.put("users", users);
        saveData();
    }

    public Optional<User> findUserByLogin(String login) {
        return getUsers().stream().filter(u -> u.getLogin().equals(login)).findFirst();
    }

    public List<Apartment> getApartments() {
        Type type = new TypeToken<ArrayList<Apartment>>() {}.getType();
        List<Apartment> apartments = gson.fromJson(gson.toJson(data.get("apartments")), type);
        return apartments != null ? apartments : new ArrayList<>();
    }

    public void saveApartment(Apartment apartment) {
        List<Apartment> apartments = getApartments();
        apartment.setNumber(nextApartmentId++);
        apartments.add(apartment);
        data.put("apartments", apartments);
        saveData();
    }

    public Optional<Apartment> findApartmentByNumber(int number) {
        return getApartments().stream().filter(a -> a.getNumber() == number).findFirst();
    }

    public List<PersonalAccount> getAccounts() {
        Type type = new TypeToken<ArrayList<PersonalAccount>>() {}.getType();
        List<PersonalAccount> accounts = gson.fromJson(gson.toJson(data.get("accounts")), type);
        return accounts != null ? accounts : new ArrayList<>();
    }

    public void saveAccount(PersonalAccount account) {
        List<PersonalAccount> accounts = getAccounts();
        accounts.removeIf(a -> a.getApartmentId() == account.getApartmentId());
        accounts.add(account);
        data.put("accounts", accounts);
        saveData();
    }

    public Optional<PersonalAccount> findAccountByApartmentId(int apartmentId) {
        return getAccounts().stream().filter(a -> a.getApartmentId() == apartmentId).findFirst();
    }

    public void updateApartment(int number, double area, int residentsCount, String ownerName) {
        List<Apartment> apartments = getApartments();
        for (int i = 0; i < apartments.size(); i++) {
            if (apartments.get(i).getNumber() == number) {
                apartments.set(i, new Apartment(number, area, residentsCount, ownerName));
                break;
            }
        }
        data.put("apartments", apartments);
        saveData();
    }

    public void deleteApartment(int number) {
        List<Apartment> apartments = getApartments();
        apartments.removeIf(a -> a.getNumber() == number);
        data.put("apartments", apartments);
        saveData();
    }
    public void deleteApartment(Apartment apartment) {
        List<Apartment> apartments = getApartments();
        apartments.removeIf(a -> a.getNumber() == apartment.getNumber());
        data.put("apartments", apartments);
        saveData();
    }

    public void deleteAccount(int apartmentId) {
        List<PersonalAccount> accounts = getAccounts();
        accounts.removeIf(a -> a.getApartmentId() == apartmentId);
        data.put("accounts", accounts);
        saveData();
    }

    public void deleteAccount(PersonalAccount account) {
        List<PersonalAccount> accounts = getAccounts();
        accounts.removeIf(a -> a.getApartmentId() == account.getApartmentId());
        data.put("accounts", accounts);
        saveData();
    }


}