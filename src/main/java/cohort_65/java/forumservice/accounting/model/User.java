package cohort_65.java.forumservice.accounting.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "login") // используем login как уникальный идентификатор
public class User {
    @Id
    private String login;
    private String password;
    private String firstName;
    private String lastName;

    private Set<Role> roles = new HashSet<>();

    public User(String login, String firstName, String lastName, String password) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }
}

