package security;

import entities.Role;
import java.util.Set;

public interface ISecurityUser {

    Set<EnumRoles> getRolesAsStrings();

    boolean verifyPassword(String pw);

    void addRole(Role role);

    void removeRole(String role);
}