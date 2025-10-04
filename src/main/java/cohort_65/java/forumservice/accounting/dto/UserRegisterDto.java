package cohort_65.java.forumservice.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserRegisterDto {
    @Setter
    String login;
    @Setter
    String password;
    @Setter
    String firstName;
    @Setter
    String lastName;

}