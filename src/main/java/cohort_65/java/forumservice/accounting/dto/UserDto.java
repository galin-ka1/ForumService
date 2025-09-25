package cohort_65.java.forumservice.accounting.dto;

import cohort_65.java.forumservice.accounting.model.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
}
