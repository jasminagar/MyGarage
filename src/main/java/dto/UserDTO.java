package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import security.EnumRoles;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String password;
    private Set<EnumRoles> roles;

    public UserDTO(String username, Set<EnumRoles> roles) {
        this.username = username;
        this.roles = roles;
    }
}