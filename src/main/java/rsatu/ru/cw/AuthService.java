package rsatu.ru.cw;

import java.util.Optional;

public class AuthService {
    private final DataService dataService;

    public AuthService(DataService dataService) {
        this.dataService = dataService;
    }

    public boolean register(String login, String password, int apartmentNumber) {
        if (dataService.findUserByLogin(login).isPresent()) {
            return false;
        }

        Optional<Apartment> apartmentOpt = dataService.findApartmentByNumber(apartmentNumber);
        if (apartmentOpt.isEmpty()) {
            return false;
        }

        boolean userExists = dataService.getUsers().stream()
                .anyMatch(u -> u.getApartmentId() != null && u.getApartmentId() == apartmentNumber);
        if (userExists) {
            return false;
        }

        User newUser = new User(0, login, password, "USER", apartmentNumber);
        dataService.saveUser(newUser);

        if (dataService.findAccountByApartmentId(apartmentNumber).isEmpty()) {
            dataService.saveAccount(new PersonalAccount(apartmentNumber, java.math.BigDecimal.ZERO));
        }

        return true;
    }

    public Optional<User> login(String login, String password) {
        Optional<User> userOpt = dataService.findUserByLogin(login);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt;
        }
        return Optional.empty();
    }
}