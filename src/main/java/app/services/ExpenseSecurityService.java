package app.services;

import app.dao.CarDao;
import app.dao.ExpenseDao;
import app.dao.UserDao;
import app.dto.ExpenseUpdateDTO;
import app.entities.Car;
import app.entities.Expense;
import app.entities.User;

public class ExpenseSecurityService {
    private final ExpenseDao expenseDao;
    private final CarDao carDao;
    private final UserDao userDao;
    private final SecurityService service;

    public ExpenseSecurityService(ExpenseDao expenseDao, CarDao carDao, UserDao userDao, SecurityService service) {
        this.expenseDao = expenseDao;
        this.carDao = carDao;
        this.userDao = userDao;
        this.service = service;
    }

    public Expense createExpenseForUser(Integer carId, String username, boolean isAdmin, Expense expense){
        Car car = carDao.findCarById(carId);
        if (car == null) {
            return null;
        }

        if (!isAdmin){
          Car ownedCar = service.findCarForUser(carId, username);
            if (ownedCar == null) {
                return null;
            }
        }

        return expenseDao.createExpense(carId, expense);
    }

    public Expense updateExpense(ExpenseUpdateDTO updateDTO, Integer carId, String userName, Integer expenseid, boolean isAdmin){
        Expense expense = expenseDao.findExpenseById(expenseid);
        if(expense == null){
            return null;
        }

        if(!expense.getId().equals(expenseid)){
            return null;
        }

        User user = userDao.findUserByUsername(userName);
        if(user == null){
            return null;
        }

        if (!isAdmin){
            Car car = service.findCarForUser(carId, userName);
            if (car == null){
                throw new SecurityException("Du har ikke adgang til bil med nummerplade: " + car.getRegistrationNumber());
            }
        }

        if (updateDTO.getDate() != null){
            expense.setDate(updateDTO.getDate());
        }

        if(updateDTO.getCategory() != null){
            expense.setCategory(updateDTO.getCategory());
        }

        if (updateDTO.getAmount() != null){
            expense.setAmount(updateDTO.getAmount());
        }

        if (updateDTO.getDescription() != null){
            expense.setDescription(updateDTO.getDescription());
        }

        return expenseDao.updateExpense(expense);
    }

    public boolean deleteExpenseForCar(Integer expenseId, String username, boolean isAdmin){
        Expense expense = expenseDao.findExpenseById(expenseId);
        if (expense == null) {
            return false;
        }

        if (isAdmin){
            expenseDao.deleteExpense(expenseId);
            return true;
        }

        Integer carId = expense.getCar().getId();
        Car ownedCar = service.findCarForUser(carId, username);
        if (ownedCar == null) {
            return false;
        }

        expenseDao.deleteExpense(expenseId);
        return true;
    }
}
