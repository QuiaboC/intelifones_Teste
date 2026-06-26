package br.com.ifpe.intelifones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(request -> {
            var config = new org.springframework.web.cors.CorsConfiguration();
            config.setAllowedOrigins(java.util.List.of("*"));
            config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
            config.setAllowedHeaders(java.util.List.of("*"));
            return config;
        }))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/uploads/**").permitAll()
            .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/api/usuarios/me", "/api/usuarios/me/imagem").authenticated()
            .requestMatchers("/api/categorias").permitAll()
            .requestMatchers("/api/categorias/{id}").permitAll()
            .requestMatchers("/api/produtos").permitAll()
            .requestMatchers("/api/carrinho/**").authenticated()
            .requestMatchers("/api/produtos/{id}").permitAll()
            .requestMatchers("/api/produtos/disponiveis").permitAll()
            .requestMatchers("/api/produtos/buscar").permitAll()
            .requestMatchers("/api/produtos/categoria/**").permitAll()
            .requestMatchers("/api/produtos/{id}/comprar").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/produtos").hasRole("VENDEDOR")
            .requestMatchers(HttpMethod.PUT, "/api/produtos/**").hasRole("VENDEDOR")
            .requestMatchers(HttpMethod.DELETE, "/api/produtos/**").hasRole("VENDEDOR")
            .requestMatchers("/api/pedidos/vendas").hasRole("VENDEDOR")
            .requestMatchers("/api/produtos/meus").hasRole("VENDEDOR")
            .requestMatchers("/api/produtos/*/repor").hasRole("VENDEDOR")
            .requestMatchers(HttpMethod.POST, "/api/categorias").hasRole("VENDEDOR")
            .requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasRole("VENDEDOR")
            .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("VENDEDOR")
            .anyRequest().authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}