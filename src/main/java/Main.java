import controllers.CarController;
import controllers.UserController;
import dao.CarDao;
import dao.UserDao;
import entities.Car;
import entities.User;
import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create();

        CarDao carDao = new CarDao();
        UserDao userDao = new UserDao();

        CarController carController = new CarController(carDao);
        UserController userController = new UserController(userDao);

        carController.addRoutes(app);
        userController.addRoutes(app);

        app.start(7070);


//            Car car = new Car();
//
//            car.setRegistrationNumber("EC74058");
//            car.setMake("AUDI");
//            car.setModel("A 4 LIMOUSINE");
//            car.setVariant("2,0 TDI");
//
//            car.setYear(2007);
//            car.setMileage(328000);
//
//            car.setVin("WAUZZZ8E67A243220");
//
//            car.setFuelType("Diesel");
//            car.setEngineVolume(1968);
//            car.setEnginePower(103);
//
//            car.setDoors(4);
//            car.setSeats(5);
//            car.setTotalWeight(1980);
//
//            car.setInspectionResult("Godkendt");
//
//            // Skal være en eksisterende User fra databasen
//            User user = new User();
//            user.setId(1);
//            car.setUser(user);
//
//            Car savedCar = carDao.createCar(car);
//
//            System.out.println("Bil oprettet!");
//            System.out.println("ID: " + savedCar.getId());
//            System.out.println("Nummerplade: " + savedCar.getRegistrationNumber());
        }


    }

