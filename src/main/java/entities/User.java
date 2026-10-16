package entities;

import lombok.Data;
import org.mindrot.jbcrypt.BCrypt;
import security.ISecurityUser;
import jakarta.persistence.*;
import security.EnumRoles;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "users")
public class User implements ISecurityUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public Set<EnumRoles> getRolesAsStrings() {
        return roles.stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.password);
    }

    @Override
    public void addRole(Role role) {
        roles.add(role);
    }

    @Override
    public void removeRole(String role) {
        roles.removeIf(r -> r.getRole().equals(role));
    }

}