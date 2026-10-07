package dao;

import entities.Modification;

import java.util.List;

public interface IModificationDao {

    Modification createModification(Integer carId, Modification modification);

    Modification findModificationById(Integer id);

    List<Modification> findModificationByCarId(Integer carId);

    Modification updateModification(Modification modification);

    void deleteModification(Integer id);
}
