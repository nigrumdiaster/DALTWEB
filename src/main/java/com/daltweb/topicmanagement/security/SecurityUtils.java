package com.daltweb.topicmanagement.security;

import com.daltweb.topicmanagement.constant.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {

    public static Optional<CustomUserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Optional.empty();
        }
        return Optional.of((CustomUserDetails) authentication.getPrincipal());
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().map(CustomUserDetails::getId).orElse(null);
    }

    public static String getCurrentUsername() {
        return getCurrentUser().map(CustomUserDetails::getUsername).orElse(null);
    }

    public static RoleType getCurrentUserRole() {
        return getCurrentUser().map(CustomUserDetails::getRole).orElse(null);
    }

    public static boolean hasRole(RoleType role) {
        return getCurrentUser().map(u -> u.getRole() == role).orElse(false);
    }
}
