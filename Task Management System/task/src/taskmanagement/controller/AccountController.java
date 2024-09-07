package taskmanagement.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import taskmanagement.controller.dto.AccountRequest;
import taskmanagement.model.entity.User;
import taskmanagement.model.service.UserService;

import java.util.regex.Pattern;

@RestController
public class AccountController {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Autowired
    private UserService userService;

    @PostMapping("/api/accounts")
    public ResponseEntity<String> createAccount(@RequestBody @Valid AccountRequest accountRequest) {
        if (accountRequest.getEmail() == null || accountRequest.getEmail().isEmpty() ||
                !EMAIL_REGEX.matcher(accountRequest.getEmail()).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or missing email");
        }

        if (accountRequest.getPassword() == null || accountRequest.getPassword().isEmpty() ||
                accountRequest.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or missing password");
        }

        String normalizedEmail = accountRequest.getEmail().toLowerCase();
        if (userService.findByEmail(normalizedEmail).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }

        User user = new User(normalizedEmail, accountRequest.getPassword());
        userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.OK).body("Account created successfully");
    }
}
