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

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Autowired
    private UserService userService;

    @PostMapping("/api/accounts")
    public ResponseEntity<String> createAccount(@RequestBody @Valid AccountRequest accountRequest) {
        if (accountRequest.getEmail() == null || accountRequest.getEmail().isBlank() ||
                !EMAIL_REGEX.matcher(accountRequest.getEmail()).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (accountRequest.getPassword() == null || accountRequest.getPassword().isBlank() ||
                accountRequest.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String normalizedEmail = accountRequest.getEmail().toLowerCase();
        if (userService.findByEmail(normalizedEmail).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = new User(normalizedEmail, accountRequest.getPassword());
        userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
