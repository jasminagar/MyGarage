package app.controllers;

import app.dao.ExpenseDao;
import app.dto.ExpenseUpdateDTO;
import app.dto.UserDTO;
import app.entities.Expense;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import app.security.EnumRoles;
import app.services.ExpenseSecurityService;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ExpenseController {

    private final ExpenseDao expenseDao;
    private final ExpenseSecurityService securityService;

    public ExpenseController(ExpenseDao expenseDao, ExpenseSecurityService securityService) {
        this.expenseDao = expenseDao;
        this.securityService = securityService;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            get("/expenses", this::getAllExpenses, EnumRoles.ADMIN);
            get("/expenses/{id}", this::getExpenseById, EnumRoles.USER, EnumRoles.ADMIN);
            get("/expenses/car/{carId}", this::getExpensesByCarId, EnumRoles.USER, EnumRoles.ADMIN);
            get("/expenses/car/{carId}/category/{category}", this::getExpensesByCarIdAndCategory, EnumRoles.USER, EnumRoles.ADMIN);
            get("/expenses/car/{carId}/total", this::getTotalExpensesForCar, EnumRoles.USER, EnumRoles.ADMIN);
            post("/expenses/{carId}/create", this::createExpense, EnumRoles.USER, EnumRoles.ADMIN);
            put("/expenses/update/{id}", this::updateExpense, EnumRoles.USER, EnumRoles.ADMIN);
            delete("/expenses/delete/{id}", this::deleteExpense, EnumRoles.USER, EnumRoles.ADMIN);
        };
    }

    private void createExpense(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));

        UserDTO tokenUser = context.attribute("user");
        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }

        Expense expense = context.bodyAsClass(Expense.class);

        boolean isAdmin = tokenUser.getRoles().stream()
                .anyMatch(enumRoles -> enumRoles == EnumRoles.ADMIN);

        Expense createdExpense = securityService.createExpenseForUser(carId, tokenUser.getUsername(), isAdmin, expense);
        if (createdExpense == null){
            context.status(404).json("Car not found or access denied");
            return;
        }

        context.status(201);
        context.json(createdExpense);
    }

    private void getAllExpenses(Context context) {
        List<Expense> expenses =
                expenseDao.findAllExpenses();

        context.json(expenses);
    }

    private void getExpenseById(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        Expense expense =
                expenseDao.findExpenseById(id);

        if (expense == null) {
            context.status(404);
            context.result("Expense not found");
            return;
        }

        context.json(expense);
    }

    private void getExpensesByCarId(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));

        List<Expense> expenses =
                expenseDao.findExpenseByCarId(carId);

        context.json(expenses);
    }

    private void getExpensesByCarIdAndCategory(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));
        String category = context.pathParam("category");

        List<Expense> expenses =
                expenseDao.findExpenseByCarIdAndCategory(carId, category);

        context.json(expenses);
    }

    private void getTotalExpensesForCar(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));

        double total =
                expenseDao.getTotalExpensesForCar(carId);

        context.json(total);
    }

    private void updateExpense(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));
        Integer expenseId = Integer.parseInt(context.pathParam("id"));

        UserDTO tokenUser = context.attribute("user");
        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }

        app.dto.ExpenseUpdateDTO expense = context.bodyAsClass(ExpenseUpdateDTO.class);

        boolean isAdmin = tokenUser.getRoles().stream()
                        .anyMatch(enumRoles -> enumRoles == EnumRoles.ADMIN);

        Expense updatedExpense =  securityService.updateExpense(expense, id, tokenUser.getUsername(), expenseId, isAdmin);

        context.json(updatedExpense);
    }

    private void deleteExpense(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        UserDTO tokenUser = context.attribute("user");
        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }

        boolean isAdmin = tokenUser.getRoles().contains(EnumRoles.ADMIN);

        boolean deleted = securityService.deleteExpenseForCar(id, tokenUser.getUsername(), isAdmin);

        if(!deleted){
            context.status(404).json("Modification not found or access denied");
            return;
        }

        context.status(204);
    }
}