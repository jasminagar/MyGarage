package entities;

import dto.UserDTO;
import lombok.Data;
import org.mindrot.jbcrypt.BCrypt;
import security.ISecurityUser;

import jakarta.persistence.*;
import security.EnumRoles;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity //Fortæller Hibernate at entitten skal mappes som tabel i database.
@Table(name = "users")
public class User implements ISecurityUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany //Brugeren kan have flere roller, og den samme rolle kan tilhøre flere brugere.
    private Set<Role> roles = new HashSet<>(); //Opretter tomt hashset, så jeg kan tilføje uden først at skulle have initialiseret sættet manuelt.
    //Set er en collection i Java collection framework, der indeholder en gruppe af unikke værdier, uden duplikater.
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        //BCrypt.gensalt() opretter en salt værdi som bruges under hashing. Ikke hashet endnu.
        //En tilfældig streng af data der tilsættes et kdoeord, så to ens hash undgås.
        //Hasher med salt værdien.
    }

    @Override
    public Set<EnumRoles> getRolesAsStrings() {
        return roles.stream()//Strean gør det muligt at behandle rollerne en af gangen.
                .map(Role::getRole)//Henter enum værdi for hvert Role objekt,
                .collect(Collectors.toSet());
    }

    @Override
    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.password);//Kontrollere at det indtastede kodeord ved login matcher hash.
    }

    @Override
    public void addRole(Role role) {
        roles.add(role);
    }

    @Override
    public void removeRole(String role) {
        roles.removeIf(r -> r.getRole().equals(role));
        //r repræsentere et Role objekt.
        //r.getRole() henter rollens enum værdi.
        //equals(role) sammenligner enum værdien med parameteren role.
    }

}