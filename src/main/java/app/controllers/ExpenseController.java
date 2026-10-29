package app.controllers;

import app.dao.ExpenseDao;
import app.dto.ExpenseUpdateDTO;
import app.dto.UserDTO;
import app.entities.Expense;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.*;
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
            put("/expenses/car/{carId}/update/{id}", this::updateExpense, EnumRoles.USER, EnumRoles.ADMIN);
            delete("/expenses/delete/{id}", this::deleteExpense, EnumRoles.USER, EnumRoles.ADMIN);
        };
    }

    private void createExpense(Context context) {
        Integer carId;

        try {
            carId = Integer.parseInt(context.pathParam("carId"));
        } catch (NumberFormatException e) {
            throw new BadRequestResponse("Invalid car ID");
        }

        if (carId <= 0) {
            throw new BadRequestResponse("Car ID must be greater than 0");
        }

        UserDTO tokenUser = context.attribute("user");

        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }

        Expense expense = context.bodyAsClass(Expense.class);

        if (expense.getAmount() < 0) {
            throw new BadRequestResponse("Expense amount cannot be negative");
        }

        boolean isAdmin =
                tokenUser.getRoles().contains(EnumRoles.ADMIN);

        Expense createdExpense = securityService.createExpenseForUser(
                carId,
                tokenUser.getUsername(),
                isAdmin,
                expense
        );

        context.status(201).json(createdExpense);
    }


    private void getAllExpenses(Context context) {
        try {
            List<Expense> expenses = expenseDao.findAllExpenses();
            context.status(200).json(expenses);
        } catch (Exception e) {
            System.err.println("Error retrieving expenses: " + e.getMessage());
            throw new InternalServerErrorResponse(
                    "Unable to retrieve expenses"
            );
        }
    }

    private void getExpenseById(Context context) {
        Integer id;

        try {
            id = Integer.parseInt(context.pathParam("id"));
        } catch (NumberFormatException e) {
            throw new BadRequestResponse("Invalid expense ID");
        }

        Expense expense = expenseDao.findExpenseById(id);

        if (expense == null) {
            throw new NotFoundResponse("Expense not found");
        }

        context.json(expense);
    }

    private void getExpensesByCarId(Context context) {
        Integer carId;

        try {
            carId = Integer.parseInt(context.pathParam("carId"));
        } catch (NumberFormatException e) {
            throw new BadRequestResponse("Invalid car ID");
        }

        if (carId <= 0) {
            throw new BadRequestResponse("Car ID must be greater than 0");
        }


        List<Expense> expenses = expenseDao.findExpenseByCarId(carId);
        context.json(expenses);
    }

    private void getExpensesByCarIdAndCategory(Context context) {
        Integer carId;

        try {
            carId = Integer.parseInt(context.pathParam("carId"));
        } catch (NumberFormatException e) {
            throw new BadRequestResponse("Invalid car ID");
        }


        if (carId <= 0) {
            throw new BadRequestResponse("Car ID must be greater than 0");
        }

        String category = context.pathParam("category");

        if (category == null || category.isBlank()) {
            throw new BadRequestResponse("Category is required");
        }

        List<Expense> expenses = expenseDao.findExpenseByCarIdAndCategory(carId, category);

        context.json(expenses);
    }

    private void getTotalExpensesForCar(Context context) {
        Integer carId;

        try {
            carId = Integer.parseInt(context.pathParam("carId"));
        } catch (NumberFormatException e) {
            throw new BadRequestResponse("Invalid car ID");
        }

        if (carId <= 0) {
            throw new BadRequestResponse("Car ID must be greater than 0");
        }

        UserDTO tokenUser = context.attribute("user");
        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }

        boolean isAdmin = tokenUser.getRoles().contains(EnumRoles.ADMIN);

        Double total = securityService.getTotalExpense(carId, tokenUser.getUsername(), isAdmin);
        context.status(200).json(total);
    }

    private void updateExpense(Context context) {
        Integer carId;
        Integer expenseId;

        try {
            carId = Integer.parseInt(context.pathParam("carId"));
            expenseId = Integer.parseInt(context.pathParam("id"));
        } catch (NumberFormatException e) {
            throw new BadRequestResponse("Invalid car ID or expense ID");
        }

        UserDTO tokenUser = context.attribute("user");

        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }

        ExpenseUpdateDTO expense = context.bodyAsClass(ExpenseUpdateDTO.class);

        boolean isAdmin = tokenUser.getRoles().contains(EnumRoles.ADMIN);

        Expense updatedExpense = securityService.updateExpense(
                expense,
                carId,
                tokenUser.getUsername(),
                expenseId,
                isAdmin
        );

        if (updatedExpense == null) {
            throw new NotFoundResponse(
                    "Expense not found or access denied"
            );
        }

        context.status(200).json(updatedExpense);
    }

    private void deleteExpense(Context context) {
        Integer id;

        try {
            id = Integer.parseInt(context.pathParam("id"));
        } catch (NumberFormatException e) {
            throw new BadRequestResponse("Invalid expense ID");
        }

        UserDTO tokenUser = context.attribute("user");

        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }

        boolean isAdmin = tokenUser.getRoles().contains(EnumRoles.ADMIN);

        boolean deleted = securityService.deleteExpenseForCar(
                id,
                tokenUser.getUsername(),
                isAdmin
        );

        if (!deleted) {
            throw new NotFoundResponse(
                    "Expense not found or access denied"
            );
        }

        context.status(204);
    }
}