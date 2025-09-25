package cohort_65.java.forumservice.accounting.dto;

import cohort_65.java.forumservice.accounting.model.Role;
import lombok.Getter;

import java.util.Set;


@Getter
public class NewUserDto {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
}
