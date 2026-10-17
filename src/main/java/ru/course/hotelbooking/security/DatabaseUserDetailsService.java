package ru.course.hotelbooking.security;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import ru.course.hotelbooking.repository.UserRepository;
@Service @RequiredArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.findByUsername(username).map(HotelUserDetails::from)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
