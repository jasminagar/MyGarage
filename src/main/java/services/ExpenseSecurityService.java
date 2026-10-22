package services;

import dao.CarDao;
import dao.ExpenseDao;
import dao.UserDao;
import dto.ExpenseUpdateDTO;
import entities.Car;
import entities.Expense;
import entities.User;

public class ExpenseSecurityService {
    private final ExpenseDao expenseDao;
    private final CarDao carDao;
    private final UserDao userDao;

    public ExpenseSecurityService(ExpenseDao expenseDao, CarDao carDao, UserDao userDao) {
        this.expenseDao = expenseDao;
        this.carDao = carDao;
        this.userDao = userDao;
    }

    public Car findCarForUser(Integer carId, String userName) {
        User user = userDao.findUserByUsername(userName);

        if (user == null) {
            return null;
        }

        return carDao.findCarByIdAndUserId(carId, user.getId());
    }

    public Expense updateExpense(ExpenseUpdateDTO updateDTO, Integer carId, String userName, Integer expenseid){
        Expense expense = expenseDao.findExpenseById(expenseid);
        if(expense == null){
            return null;
        }

        if(!expense.getId().equals(expenseid)){
            return null;
        }

        User user = userDao.findUserByUsername(userName);
        return expense;
    }
}
