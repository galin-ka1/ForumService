package cohort_65.java.forumservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.stereotype.Component;

import static cohort_65.java.forumservice.accounting.model.Role.MODERATOR;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

@Component
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/account/register").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/account/user/{login}")
                .access(new WebExpressionAuthorizationManager(
                        "hasRole('ADMIN') or authentication.name == #login"))
                .requestMatchers(HttpMethod.PUT, "/account/user/{login}")
                .access(new WebExpressionAuthorizationManager(
                        "authentication.name == #login"))
                .requestMatchers(HttpMethod.PUT, "/account/user/{login}/role/{role}")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/account/user/{login}/role/{role}")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/account/user/{login}")
                .access(new WebExpressionAuthorizationManager(
                        "hasRole('ADMIN') or authentication.name == #login"))
                .requestMatchers(HttpMethod.PUT, "/account/password").authenticated()
                .requestMatchers(HttpMethod.POST, "/forum/post/{author}")
                .access(new WebExpressionAuthorizationManager("isAuthenticated() and authentication.name == #author"))
                .requestMatchers(HttpMethod.PUT, "/forum/post/{id}/like")
                .access(new WebExpressionAuthorizationManager("isAuthenticated()"))
                .requestMatchers(HttpMethod.DELETE, "/forum/post/{id}")
                .access(new WebExpressionAuthorizationManager(
                        "isAuthenticated() and (hasRole('MODERATOR') or authentication.name == @postSecurity.getPostAuthor(#id))"))
                .requestMatchers(HttpMethod.PUT, "/forum/post/{id}")
                .access(new WebExpressionAuthorizationManager(
                        "authentication.name == @postSecurity.getPostAuthor(#id)"
                ))
                .requestMatchers(HttpMethod.PUT, "/forum/post/{id}/comment/{author}")
                .access(new WebExpressionAuthorizationManager("isAuthenticated()"))
                .requestMatchers(HttpMethod.GET, "/forum/post/").permitAll()
                .requestMatchers(HttpMethod.GET, "/forum/posts/").permitAll()
                .requestMatchers(HttpMethod.POST, "/forum/posts/").permitAll()

                .anyRequest().authenticated());
        return http.build();
    }
}


