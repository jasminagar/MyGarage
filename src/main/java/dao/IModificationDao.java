package dao;

import entities.Modification;

import java.util.List;

public interface IModificationDao {

    Modification createModification(Modification modification);

    Modification findModificationById(Integer id);

    List<Modification> findAllModifications();

    List<Modification> findModificationByCarId(Integer carId);

    Modification updateModification(Modification modification);

    void deleteModification(Integer id);
}
