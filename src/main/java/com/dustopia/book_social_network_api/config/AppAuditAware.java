package com.dustopia.book_social_network_api.config;

import com.dustopia.book_social_network_api.model.entity.User;
import com.dustopia.book_social_network_api.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AppAuditAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        return Optional.ofNullable(user.getId());
    }

}
