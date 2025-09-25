package cohort_65.java.forumservice.accounting.controller;

import cohort_65.java.forumservice.accounting.dto.NewUserDto;
import cohort_65.java.forumservice.accounting.dto.UserDto;
import cohort_65.java.forumservice.accounting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/account/register")
    public UserDto register(@RequestBody NewUserDto newUserDto) {
        return userService.registerUser(newUserDto);
    }

    @DeleteMapping("/account/user/{login}")
    public UserDto removeUser(@PathVariable String login) {
        return userService.deleteUserByLogin(login);
    }

    @GetMapping("/account/user/{login}")
    public UserDto getUser(@PathVariable String login) {
        return userService.getUserByLogin(login);
    }

    @PutMapping("/account/user/{login}")
    public UserDto updateUser(@PathVariable String login, @RequestBody NewUserDto newUserDto) {
        return userService.updateUserByLogin(newUserDto, login);

    }

    @PutMapping("/account/user/{login}/role/{role}")
    public UserDto addRole(@PathVariable String login,
                           @PathVariable String role) {
        return userService.addRole(login, role);
    }

    @DeleteMapping("/account/user/{login}/role/{role}")
    public UserDto removeRole(@PathVariable String login,
                              @PathVariable String role) {
        return userService.deleteRoleByLogin(login,role);
    }





}