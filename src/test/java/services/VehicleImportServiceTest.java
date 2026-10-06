package services;

import dao.CarDao;
import dao.ServiceRecordDao;
import dto.VehicleDTO;
import entities.Car;
import entities.ServiceRecord;
import entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;


class VehicleImportServiceTest {

    private VehicleApiReader apiReader;
    private ConvertToEntity converter;
    private CarDao carDao;
    private ServiceRecordDao serviceRecordDao;

    private ImportVehicleService vehicleImportService;

    @BeforeEach
    void setUp() {
        apiReader = mock(VehicleApiReader.class);
        converter = mock(ConvertToEntity.class);
        carDao = mock(CarDao.class);
        serviceRecordDao = mock(ServiceRecordDao.class);

        vehicleImportService = new ImportVehicleService(
                apiReader,
                converter,
                carDao,
                serviceRecordDao
        );
    }

    @Test
    void importVehicle_shouldCreateCarAndServiceRecord() {

        String registrationNumber = "EC74058";
        String json = "{\"registration_number\":\"EC74058\"}";

        User user = new User(2, "test");

        VehicleDTO vehicleDTO = new VehicleDTO();

        Car car = new Car();
        car.setRegistrationNumber("EC74058");

        Car savedCar = new Car();
        savedCar.setId(2);
        savedCar.setRegistrationNumber("EC74058");
        savedCar.setUser(user);

        ServiceRecord serviceRecord = new ServiceRecord();
        serviceRecord.setCar(savedCar);

        when(apiReader.getVehicle(registrationNumber))
                .thenReturn(json);

        when(apiReader.convertFromJson(json))
                .thenReturn(vehicleDTO);

        when(converter.convertToCarEntity(vehicleDTO))
                .thenReturn(car);

        when(carDao.createCar(car))
                .thenReturn(savedCar);

        when(converter.convertToServiceRecord(vehicleDTO, savedCar))
                .thenReturn(serviceRecord);

        Car result =
                vehicleImportService.importVehicle(
                        registrationNumber,
                        user
                );

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(2));
        assertThat(result.getRegistrationNumber(), is("EC74058"));
        assertThat(result.getUser(), is(user));

        verify(apiReader).getVehicle(registrationNumber);
        verify(apiReader).convertFromJson(json);

        verify(converter).convertToCarEntity(vehicleDTO);

        verify(carDao).createCar(car);

        verify(converter)
                .convertToServiceRecord(vehicleDTO, savedCar);

        verify(serviceRecordDao)
                .createServiceRecord(serviceRecord);
    }
}