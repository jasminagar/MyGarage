package security;

import dto.UserDTO;
import entities.Role;
import entities.User;

public interface ISecurityDAO {

    User getVerifiedUser(String username, String password);

    User createUser(String username, String password);

    Role createRole(String role);

    User addUserRole(String username, String role);
}