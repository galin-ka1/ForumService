package cohort_65.java.forumservice.accounting.service;

import cohort_65.java.forumservice.accounting.dao.UserAccountRepository;
import cohort_65.java.forumservice.accounting.dto.UserDto;
import cohort_65.java.forumservice.accounting.dto.UserRegisterDto;
import cohort_65.java.forumservice.accounting.dto.UserUpdateDto;
import cohort_65.java.forumservice.accounting.model.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserAccountServiceTest {

    private UserAccountService userAccountService;
    private UserAccountRepository userAccountRepository;

    @BeforeEach
    void setUp() {
        userAccountRepository = Mockito.mock(UserAccountRepository.class);
        userAccountService = new UserAccountServiceImpl(userAccountRepository);
    }

    @Test
    void register_Positive() {
        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setLogin("john1234");
        registerDto.setFirstName("John");
        registerDto.setLastName("Langkoff");
        registerDto.setPassword("password1");

        when(userAccountRepository.findByLogin("john1234")).thenReturn(Optional.empty());
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userAccountService.register(registerDto);

        assertThat(result).isNotNull();
        assertThat(result.getLogin()).isEqualTo("john1234");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getRoles()).contains("USER");

        verify(userAccountRepository).save(any(UserAccount.class));
    }

    @Test
    void register_AlreadyExists() {
        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setLogin("john1234");

        when(userAccountRepository.findByLogin("john1234")).thenReturn(Optional.of(new UserAccount()));

        assertThrows(RuntimeException.class,
                () -> userAccountService.register(registerDto));

        verify(userAccountRepository).findByLogin("john1234");
        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void getUserByLogin_Positive() {
        UserAccount user1 = new UserAccount("john1234", "password1", "John", "Langkoff");
        UserAccount user2 = new UserAccount("john12345", "password2", "John", "Langkoff");
        when(userAccountRepository.findByLogin("john1234")).thenReturn(Optional.of(user1));

        UserDto result = userAccountService.getUserByLogin("john1234");

        assertThat(result).isNotNull();
        assertThat(result.getLogin()).isEqualTo("john1234");
        assertThat(result.getRoles()).contains("USER");

        verify(userAccountRepository).findByLogin("john1234");
    }

    @Test
    void getUserByLogin_NotFound() {
        when(userAccountRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userAccountService.getUserByLogin("unknown"));

        verify(userAccountRepository).findByLogin("unknown");
    }

    @Test
    void removeUserByLogin_Positive() {
        UserAccount user = new UserAccount("john1234", "password", "John", "Langkoff");

        when(userAccountRepository.findByLogin("john1234")).thenReturn(Optional.of(user));

        UserDto result = userAccountService.removeUserByLogin("john1234");

        assertThat(result).isNotNull();
        assertThat(result.getLogin()).isEqualTo("john1234");

        verify(userAccountRepository).findByLogin("john1234");
        verify(userAccountRepository).delete(user);
    }

    @Test
    void removeUserByLogin_NotFound() {
        when(userAccountRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userAccountService.removeUserByLogin("unknown"));

        verify(userAccountRepository).findByLogin("unknown");
        verify(userAccountRepository, never()).delete(any());
    }

    @Test
    void updateUserByLogin_Positive() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFirstName("Johnny");
        updateDto.setLastName("Langkoffi");

        UserAccount existingUser = new UserAccount("john1234", "password", "John", "Langkoff");

        when(userAccountRepository.findByLogin("john1234")).thenReturn(Optional.of(existingUser));
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userAccountService.updateUserByLogin("john1234", updateDto);

        assertThat(result.getFirstName()).isEqualTo("Johnny");
        assertThat(result.getLastName()).isEqualTo("Langkoffi");

        verify(userAccountRepository).findByLogin("john1234");
        verify(userAccountRepository).save(any(UserAccount.class));
    }

    @Test
    void updateUserByLogin_NotFound() {
        when(userAccountRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        UserUpdateDto updateDto = new UserUpdateDto();
        assertThrows(RuntimeException.class,
                () -> userAccountService.updateUserByLogin("unknown", updateDto));

        verify(userAccountRepository).findByLogin("unknown");
        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void changeRoleForUser_AddRole() {
        UserAccount user = new UserAccount("john1234", "password", "John", "Langkoff");

        when(userAccountRepository.findByLogin("john1234")).thenReturn(Optional.of(user));
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userAccountService.changeRoleForUser("john1234", "ADMIN", true);

        assertThat(result.getRoles()).contains("ADMIN");
        verify(userAccountRepository).save(any(UserAccount.class));
    }

    @Test
    void changeRoleForUser_RemoveRole() {
        UserAccount user = new UserAccount("john1234", "password", "John", "Langkoff");
        user.addRole("ADMIN");

        when(userAccountRepository.findByLogin("john1234")).thenReturn(Optional.of(user));
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userAccountService.changeRoleForUser("john1234", "ADMIN", false);

        assertThat(result.getRoles()).doesNotContain("ADMIN");
        verify(userAccountRepository).save(any(UserAccount.class));
    }

    @Test
    void changeRoleForUser_UserNotFound() {
        when(userAccountRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userAccountService.changeRoleForUser("unknown", "ADMIN", true));

        verify(userAccountRepository).findByLogin("unknown");
        verify(userAccountRepository, never()).save(any());
    }
}
