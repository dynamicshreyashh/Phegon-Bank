package com.phegon.phegonbank.security;

import com.phegon.phegonbank.auth_users.entity.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@Builder
@Data
public class AuthUser implements UserDetails {
    private User user;

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream().map(r -> (GrantedAuthority) () -> r.getName()).toList();
    }
    @Override public String getPassword() { return user.getPassword(); }
    @Override public String getUsername() { return user.getEmail(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return user.isActive(); }
}
