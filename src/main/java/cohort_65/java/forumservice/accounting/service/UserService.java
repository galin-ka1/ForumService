package cohort_65.java.forumservice.accounting.service;

import cohort_65.java.forumservice.accounting.dto.NewUserDto;
import cohort_65.java.forumservice.accounting.dto.UserDto;

import java.util.Set;

public interface UserService {

    UserDto registerUser(NewUserDto newUserDto);
    UserDto deleteUserByLogin(String login);
    UserDto getUserByLogin(String login);
    UserDto updateUserByLogin(NewUserDto newUserDto, String login);
    UserDto addRole(String login, String role);
    UserDto deleteRoleByLogin(String login, String role);
}