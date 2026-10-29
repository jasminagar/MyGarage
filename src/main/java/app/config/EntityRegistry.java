package app.config;

import app.entities.*;
import org.hibernate.cfg.Configuration;

public class EntityRegistry {
    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(Car.class);
        configuration.addAnnotatedClass(Expense.class);
        configuration.addAnnotatedClass(Modification.class);
        configuration.addAnnotatedClass(InspectionRecord.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
    }
}
