package taskmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanagement.controller.dto.TokenResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtEncoder jwtEncoder;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .claim("scope", authorities)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claimsSet))
                .getTokenValue();
        return ResponseEntity.ok().body(new TokenResponse(token));
    }
}
