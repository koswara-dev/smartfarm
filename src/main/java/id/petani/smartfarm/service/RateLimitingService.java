package id.petani.smartfarm.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitingService {

    private static final int MAX_ATTEMPTS = 5; // Max login attempts
    private static final long TIME_WINDOW_MILLIS = 5 * 60 * 1000; // 5 minutes in milliseconds

    private final ConcurrentHashMap<String, LoginAttempt> loginAttemptMap = new ConcurrentHashMap<>();

    public boolean isRateLimited(String key) {
        LoginAttempt attempt = loginAttemptMap.computeIfAbsent(key, k -> new LoginAttempt());

        long currentTime = System.currentTimeMillis();

        // If the time window has passed, reset the attempt count for a new window.
        if (currentTime - attempt.getLastAttemptTime() > TIME_WINDOW_MILLIS) {
            attempt.reset(currentTime); // Resets attempts to 1 and updates timestamp.
        } else {
            // Otherwise, increment the attempt count within the current window.
            attempt.incrementAttempts();
        }

        // Check if the current number of attempts exceeds the maximum allowed.
        return attempt.getAttempts() > MAX_ATTEMPTS;
    }

    public void recordSuccessfulAttempt(String key) {
        loginAttemptMap.remove(key);
    }

    private static class LoginAttempt {
        private final AtomicInteger attempts;
        private long lastAttemptTime;

        public LoginAttempt() {
            this.attempts = new AtomicInteger(0);
            this.lastAttemptTime = System.currentTimeMillis();
        }

        public int getAttempts() {
            return attempts.get();
        }

        public long getLastAttemptTime() {
            return lastAttemptTime;
        }

        public void incrementAttempts() {
            attempts.incrementAndGet();
            lastAttemptTime = System.currentTimeMillis();
        }

        // Resets attempts to 1 (for the current attempt) and updates the last attempt time.
        public void reset(long currentTime) {
            attempts.set(1);
            lastAttemptTime = currentTime;
        }
    }
}
