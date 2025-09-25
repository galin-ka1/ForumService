package cohort_65.java.forumservice.accounting.service;

import cohort_65.java.forumservice.accounting.dao.UserRepository;

import cohort_65.java.forumservice.accounting.dto.NewUserDto;
import cohort_65.java.forumservice.accounting.dto.UserDto;
import cohort_65.java.forumservice.accounting.dto.exception.UserNotFoundException;
import cohort_65.java.forumservice.accounting.model.Role;
import cohort_65.java.forumservice.accounting.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;
    final ModelMapper modelMapper;

    @Override
    public UserDto registerUser(NewUserDto newUserDto) {
        User user = new User(newUserDto.getLogin(),
                newUserDto.getFirstName(), newUserDto.getLastName(), newUserDto.getPassword());
        user = userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }
    @Override
    public UserDto deleteUserByLogin(String login) {
        User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByLogin(String login) {
        User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserDto.class);
    }
    @Override
    public UserDto updateUserByLogin(NewUserDto newUserDto, String login) {
        User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        String firstName = newUserDto.getFirstName();
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        String lastName = newUserDto.getLastName();
        if (lastName != null) {
            user.setLastName(lastName);
        }

        user = userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }
    @Override
    public UserDto addRole(String login, String role) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(UserNotFoundException::new);

        user.getRoles().add(Role.valueOf(role.toUpperCase())); // преобразуем строку в enum
        user = userRepository.save(user);

        return modelMapper.map(user, UserDto.class);
    }


    @Override
    public UserDto deleteRoleByLogin(String login, String role) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(UserNotFoundException::new);

        user.getRoles().remove(Role.valueOf(role.toUpperCase()));
        user = userRepository.save(user);

        return modelMapper.map(user, UserDto.class);
    }


}