package ru.course.hotelbooking.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.course.hotelbooking.model.AppUser;
import java.util.Collection;
import java.util.List;

public record HotelUserDetails(Long id, String username, String password,
                               Collection<? extends GrantedAuthority> authorities) implements UserDetails {
    public static HotelUserDetails from(AppUser user) {
        return new HotelUserDetails(user.getId(), user.getUsername(), user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return password; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
}
