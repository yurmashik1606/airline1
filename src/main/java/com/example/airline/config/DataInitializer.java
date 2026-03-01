package com.example.airline.config;

import com.example.airline.models.*;
import com.example.airline.repositories.*;
import com.example.airline.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * Инициализатор начальных данных.
 * При первом запуске приложения автоматически заполняет базу данных
 * тестовыми записями: аэропортами, воздушными судами, рейсами и пользователями.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final AirportRepository airportRepository;
    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public DataInitializer(AirportRepository airportRepository,
                           AircraftRepository aircraftRepository,
                           FlightRepository flightRepository,
                           UserRepository userRepository,
                           UserService userService) {
        this.airportRepository = airportRepository;
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Заполняет базу данных начальными данными при первом запуске.
     * Повторный запуск не создаёт дублирующихся записей.
     *
     * @param args аргументы командной строки
     */
    @Override
    public void run(String... args) {

        // Пользователи
        if (userRepository.findByLogin("admin").isEmpty()) {
            User admin = new User();
            admin.setLogin("admin");
            admin.setPasswordHash("admin123");
            admin.setFirstName("Администратор");
            admin.setLastName("Системы");
            admin.setRole("ROLE_ADMIN");
            userService.saveUser(admin);
        }

        if (userRepository.findByLogin("dispatcher").isEmpty()) {
            User dispatcher = new User();
            dispatcher.setLogin("dispatcher");
            dispatcher.setPasswordHash("disp123");
            dispatcher.setFirstName("Иван");
            dispatcher.setLastName("Иванов");
            dispatcher.setRole("ROLE_DISPATCHER");
            userService.saveUser(dispatcher);
        }

        // Аэропорты
        Airport svo = createOrGetAirport("SVO", "Шереметьево", "Москва", "Россия");
        Airport led = createOrGetAirport("LED", "Пулково", "Санкт-Петербург", "Россия");
        Airport aer = createOrGetAirport("AER", "Адлер", "Сочи", "Россия");
        Airport svx = createOrGetAirport("SVX", "Кольцово", "Екатеринбург", "Россия");
        Airport ovb = createOrGetAirport("OVB", "Толмачёво", "Новосибирск", "Россия");

        // Воздушные суда
        Aircraft b737 = createOrGetAircraft("Boeing 737-800", "VP-BXA", 150, 12, "В эксплуатации");
        Aircraft a320 = createOrGetAircraft("Airbus A320", "VP-BKQ", 144, 8, "В эксплуатации");
        Aircraft ssj  = createOrGetAircraft("Sukhoi Superjet 100", "RA-89010", 93, 6, "В эксплуатации");
        createOrGetAircraft("Boeing 737-800", "VP-BNW", 150, 12, "На техобслуживании");

        // Рейсы
        createOrGetFlight("SU-1001", svo, led, b737, LocalDateTime.now().plusDays(1).withHour(7).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(9).withMinute(10), "SCHEDULED");

        createOrGetFlight("SU-1002", led, svo, b737, LocalDateTime.now().plusDays(1).withHour(10).withMinute(30),
                LocalDateTime.now().plusDays(1).withHour(12).withMinute(40), "SCHEDULED");

        createOrGetFlight("SU-2001", svo, aer, a320, LocalDateTime.now().plusDays(1).withHour(8).withMinute(15),
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(45), "CHECK_IN");

        createOrGetFlight("SU-3001", svo, svx, ssj, LocalDateTime.now().withHour(6).withMinute(0),
                LocalDateTime.now().withHour(8).withMinute(20), "DEPARTED");

        createOrGetFlight("SU-4001", ovb, svo, a320, LocalDateTime.now().minusDays(1).withHour(14).withMinute(0),
                LocalDateTime.now().minusDays(1).withHour(18).withMinute(30), "ARRIVED");

        createOrGetFlight("SU-5001", svo, ovb, b737, LocalDateTime.now().plusDays(2).withHour(11).withMinute(0),
                LocalDateTime.now().plusDays(2).withHour(15).withMinute(30), "DELAYED");

        createOrGetFlight("SU-6001", led, aer, ssj, LocalDateTime.now().plusDays(3).withHour(9).withMinute(0),
                LocalDateTime.now().plusDays(3).withHour(11).withMinute(50), "SCHEDULED");

        createOrGetFlight("SU-7001", aer, svo, a320, LocalDateTime.now().plusDays(2).withHour(13).withMinute(0),
                LocalDateTime.now().plusDays(2).withHour(15).withMinute(30), "SCHEDULED");
    }

    private Airport createOrGetAirport(String iata, String name, String city, String country) {
        return airportRepository.findByIataCode(iata).orElseGet(() -> {
            Airport a = new Airport();
            a.setIataCode(iata);
            a.setName(name);
            a.setCity(city);
            a.setCountry(country);
            return airportRepository.save(a);
        });
    }

    private Aircraft createOrGetAircraft(String model, String reg, int eco, int biz, String status) {
        return aircraftRepository.findByRegistrationNumber(reg).orElseGet(() -> {
            Aircraft a = new Aircraft();
            a.setModel(model);
            a.setRegistrationNumber(reg);
            a.setEconomySeats(eco);
            a.setBusinessSeats(biz);
            a.setStatus(status);
            return aircraftRepository.save(a);
        });
    }

    private void createOrGetFlight(String number, Airport dep, Airport arr, Aircraft ac,
                                    LocalDateTime planned_dep, LocalDateTime planned_arr,
                                    String status) {
        if (flightRepository.findByFlightNumber(number).isEmpty()) {
            Flight f = new Flight();
            f.setFlightNumber(number);
            f.setDepartureAirport(dep);
            f.setArrivalAirport(arr);
            f.setAircraft(ac);
            f.setPlannedDeparture(planned_dep);
            f.setPlannedArrival(planned_arr);
            f.setStatus(status);
            flightRepository.save(f);
        }
    }
}
