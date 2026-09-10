package config;

import entities.*;
import org.hibernate.cfg.Configuration;

public class EntityRegistry {
    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(Car.class);
        configuration.addAnnotatedClass(Expense.class);
        configuration.addAnnotatedClass(Modification.class);
        configuration.addAnnotatedClass(ServiceRecord.class);
        configuration.addAnnotatedClass(User.class);
    }
}
