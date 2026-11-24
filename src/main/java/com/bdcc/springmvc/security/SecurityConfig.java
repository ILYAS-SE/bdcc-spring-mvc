package com.bdcc.springmvc.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // active @PreAuthorize (technique 2 du prof)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Strategie "in memory authentication" (les mots de passe sont haches avec BCrypt). */
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder encoder) {
        String hashed = encoder.encode("1234");
        System.out.println("Mot de passe 1234 hache avec BCrypt : " + hashed);
        return new InMemoryUserDetailsManager(
                User.withUsername("user1").password(hashed).roles("USER").build(),
                User.withUsername("user2").password(hashed).roles("USER").build(),
                User.withUsername("admin").password(hashed).roles("USER", "ADMIN").build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Formulaire d'authentification personnalise (/login)
            .formLogin(fl -> fl
                .loginPage("/login")
                .defaultSuccessUrl("/user/index", true)
                .permitAll())
            .logout(lo -> lo
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll())
            .authorizeHttpRequests(ar -> ar
                .requestMatchers("/webjars/**").permitAll()      // Bootstrap
                .requestMatchers("/login").permitAll()
                .requestMatchers(PathRequest.toH2Console()).hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated())
            // Protection CSRF ACTIVE par defaut (jeton ajoute dans les formulaires Thymeleaf)
            // Seule la console H2 (dev) en est exclue.
            .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            // Redirection au lieu d'une page 403 brute
            .exceptionHandling(eh -> eh.accessDeniedPage("/notAuthorized"));
        return http.build();
    }
}
