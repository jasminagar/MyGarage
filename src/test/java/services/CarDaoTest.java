package services;

import config.HibernateConfig;
import dao.CarDao;
import entities.Car;
import entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class CarDaoTest {
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    private CarDao carDao;
    private User testUser;

    @BeforeEach
    void setUp(){
        carDao = new CarDao();

        testUser = new User(2, "test");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(testUser);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
            { em.getTransaction().rollback();
            } throw e;}
        finally {
            em.close(); }
    }

@AfterAll
   static void tearDown(){
        if (emf.isOpen()){
            emf.close();
        }
}

    private Car createTestCar() {
        Car car = new Car();
        car.setMake("AUDI");
        car.setModel("A 4 LIMOUSINE");
        car.setVariant("2,0 TDI");
        car.setYear(2007);
        car.setMileage(328000);
        car.setRegistrationNumber("TEST123");
        car.setVin("TESTVIN123456");
        car.setFuelType("Diesel");
        car.setEngineVolume(1968);
        car.setEnginePower(103);
        car.setDoors(4);
        car.setSeats(5);
        car.setTotalWeight(1980);
        car.setInspectionResult("Godkendt");
        car.setUser(testUser);
        return car;
    }

    @Test
    void createCar_shouldSaveCarToDatabase() {
        Car car = createTestCar();
        Car savedCar = carDao.createCar(car);
        assertThat(savedCar, is(notNullValue()));
        assertThat(savedCar.getId(), is(notNullValue()));
        assertThat(savedCar.getMake(), is("AUDI"));
        assertThat(savedCar.getModel(), is("A 4 LIMOUSINE"));
        assertThat(savedCar.getRegistrationNumber(), is("TEST123"));
    }

    @Test
    void findCarById_shouldReturnCorrectCar() {
        Car savedCar = carDao.createCar(createTestCar());
        Car foundCar = carDao.findCarById(savedCar.getId());
        assertThat(foundCar, is(notNullValue()));
        assertThat(foundCar.getId(), is(savedCar.getId()));
        assertThat(foundCar.getRegistrationNumber(), is("TEST123"));
        assertThat(foundCar.getMake(), is("AUDI"));
    }

    @Test
    void findCarById_shouldReturnNullWhenCarDoesNotExist() {
        Car foundCar = carDao.findCarById(999999);
        assertThat(foundCar, is(nullValue()));
    }

    @Test void findAllCars_shouldReturnCarsFromDatabase() {
        Car car1 = createTestCar();
        car1.setRegistrationNumber("TEST001");
        Car car2 = createTestCar();
        car2.setRegistrationNumber("TEST002");
        carDao.createCar(car1); carDao.createCar(car2);
        List<Car> cars = carDao.findAllcars();
        assertThat(cars, is(notNullValue()));
        assertThat(cars, is(not(empty())));
        assertThat( cars, hasItems( hasProperty("registrationNumber",
                is("TEST001")),
                hasProperty("registrationNumber",
                        is("TEST002")) ) );
    }

    @Test
    void updateCar_shouldUpdateExistingCar() {
        Car car = carDao.createCar(createTestCar());
        car.setMake("BMW");
        car.setModel("320D");
        car.setMileage(350000);
        Car updatedCar = carDao.updateCar(car);
        assertThat(updatedCar, is(notNullValue()));
        assertThat(updatedCar.getId(), is(car.getId()));
        assertThat(updatedCar.getMake(), is("BMW"));
        assertThat(updatedCar.getModel(), is("320D"));
        assertThat(updatedCar.getMileage(), is(350000));
    }
    @Test
    void deleteCar_shouldRemoveCarFromDatabase() {
        Car car = carDao.createCar(createTestCar());
        Integer carId = car.getId(); carDao.deleteCar(car);
        Car deletedCar = carDao.findCarById(carId);
        assertThat(deletedCar, is(nullValue()));
    }
    @Test
    void findCarByUserId_shouldReturnCarsBelongingToUser() {
        Car car1 = createTestCar();
        car1.setRegistrationNumber("USERCAR1");
        Car car2 = createTestCar();
        car2.setRegistrationNumber("USERCAR2");
        carDao.createCar(car1);
        carDao.createCar(car2);

        List<Car> cars = carDao.findCarByUserId(testUser.getId());
        assertThat(cars, is(notNullValue()));
        assertThat(cars, hasSize(2));
        assertThat( cars, hasItems( hasProperty("registrationNumber",
                is("USERCAR1")),
                hasProperty("registrationNumber",
                        is("USERCAR2")) ) );
    }

    @Test
    void findCarByUserId_shouldReturnEmptyListWhenUserHasNoCars() {
        List<Car> cars = carDao.findCarByUserId(testUser.getId());
        assertThat(cars, is(notNullValue()));
        assertThat(cars, is(empty()));
    }

}
