package co.edu.eci.blueprints.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Set;

@Service
public class InMemoryUserService {
    private final Map<String, String> users; // username -> hash
    private final Map<String, Set<String>> scopes; // username -> scopes
    private final PasswordEncoder encoder;

    public InMemoryUserService(PasswordEncoder encoder) {
        this.encoder = encoder;
        this.users = Map.of(
                "student", encoder.encode("student123"),
                "assistant", encoder.encode("assistant123")
        );
        this.scopes = Map.of(
                "student", Set.of("blueprints.read"),
                "assistant", Set.of("blueprints.read", "blueprints.write")
        );
    }

    public boolean isValid(String username, String rawPassword) {
        String hash = users.get(username);
        return hash != null && encoder.matches(rawPassword, hash);
    }

    public Set<String> getScopes(String username) {
        return scopes.getOrDefault(username, Set.of());
    }
}