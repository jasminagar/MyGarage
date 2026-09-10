package dao;

import entities.ServiceRecord;

import java.util.List;

public interface IServiceRecordDao {

    ServiceRecord createServiceRecord(ServiceRecord serviceRecord);

    ServiceRecord findServiceRecordById(Integer id);

    List<ServiceRecord> findAllServiceRecords();

    List<ServiceRecord> findServiceRecordByCarId(Integer carId);

    ServiceRecord updateServiceRecord(ServiceRecord serviceRecord);

    void deleteServiceRecord(Integer id);

    List<ServiceRecord> findServiceRecordByCarIdOrderByDateDesc(Integer carId);
}
