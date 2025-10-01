package cohort_65.java.forumservice.accounting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UserRegisterDto {
    String login;
    String password;
    @Setter
    String firstName;
    @Setter
    String lastName;
}