package org.jarvis.security.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static long getCurrentUserId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
