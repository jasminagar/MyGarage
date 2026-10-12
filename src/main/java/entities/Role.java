package entities;

import jakarta.persistence.*;
import lombok.Data;
import security.EnumRoles;

@Data
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private EnumRoles role;

    public Role(EnumRoles role) {
        this.role = role;
    }

    public Role() {
    }
}
