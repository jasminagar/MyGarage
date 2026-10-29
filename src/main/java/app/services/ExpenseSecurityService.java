package app.services;

import app.dao.CarDao;
import app.dao.ExpenseDao;
import app.dao.UserDao;
import app.dto.ExpenseUpdateDTO;
import app.entities.Car;
import app.entities.Expense;
import app.entities.User;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;

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


    public Expense createExpenseForUser(
            Integer carId,
            String username,
            boolean isAdmin,
            Expense expense) {

        Car car = carDao.findCarById(carId);

        if (car == null) {
            throw new NotFoundResponse(
                    "Car not found with ID: " + carId
            );
        }

        if (!isAdmin) {
            Car ownedCar = service.findCarForUser(carId, username);

            if (ownedCar == null) {
                throw new ForbiddenResponse(
                        "You do not have access to car ID: " + carId
                );
            }
        }

        return expenseDao.createExpense(carId, expense);
    }

    public Expense updateExpense(
            ExpenseUpdateDTO updateDTO,
            Integer carId,
            String userName,
            Integer expenseId,
            boolean isAdmin) {

        Expense expense = expenseDao.findExpenseById(expenseId);

        if (expense == null) {
            throw new NotFoundResponse(
                    "Expense not found with ID: " + expenseId);
        }

        User user = userDao.findUserByUsername(userName);

        if (user == null) {
            throw new UnauthorizedResponse(
                    "Authenticated user not found"
            );
        }

        if (expense.getCar() == null ||
                !expense.getCar().getId().equals(carId)) {
            throw new BadRequestResponse(
                    "Expense does not belong to car ID: " + carId
            );
        }

        if (!isAdmin) {
            Car car = service.findCarForUser(carId, userName);

            if (car == null) {
                throw new ForbiddenResponse(
                        "You do not have access to car ID: " + carId
                );
            }
        }

        if (updateDTO.getDate() != null) {
            expense.setDate(updateDTO.getDate());
        }

        if (updateDTO.getCategory() != null) {
            expense.setCategory(updateDTO.getCategory());
        }

        if (updateDTO.getAmount() != null) {
            expense.setAmount(updateDTO.getAmount());
        }

        if (updateDTO.getDescription() != null) {
            expense.setDescription(updateDTO.getDescription());
        }

        return expenseDao.updateExpense(expense);
    }



    public boolean deleteExpenseForCar(
            Integer expenseId,
            String username,
            boolean isAdmin) {

        Expense expense = expenseDao.findExpenseById(expenseId);

        if (expense == null) {
            throw new NotFoundResponse(
                    "Expense not found with ID: " + expenseId
            );
        }

        if (isAdmin) {
            expenseDao.deleteExpense(expenseId);
            return true;
        }

        if (expense.getCar() == null) {
            throw new NotFoundResponse(
                    "Car associated with expense was not found"
            );
        }

        Integer carId = expense.getCar().getId();
        Car ownedCar = service.findCarForUser(carId, username);

        if (ownedCar == null) {
            throw new ForbiddenResponse(
                    "You do not have access to expense ID: " + expenseId
            );
        }

        expenseDao.deleteExpense(expenseId);
        return true;
    }

}
