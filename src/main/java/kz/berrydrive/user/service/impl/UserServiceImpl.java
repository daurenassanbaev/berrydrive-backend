package kz.berrydrive.user.service.impl;

import kz.berrydrive.common.exception.AlreadyExistsException;
import kz.berrydrive.user.entity.User;
import kz.berrydrive.user.repository.UserRepository;
import kz.berrydrive.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void createUser(User user) {
        log.info("Creating user: %s".formatted(user));
        checkUserExistence(user.getEmail());
        userRepository.save(user);
    }

    private void checkUserExistence(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AlreadyExistsException("User with email %s already exists ".formatted(email));
        }
    }
}
