package security;

import entities.Role;
import entities.User;

public interface ISecurityDAO {

    User getVerifiedUser(String username, String password);

    User createUser(String username, String password);

    Role createRole(EnumRoles role);

    User addUserRole(String username, EnumRoles role);
}