package com.example.chatserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration.
 * 
 * This configuration provides minimal security settings for development.
 * It permits WebSocket connections and static resources without authentication.
 * 
 * Development vs Production:
 * - Development: Relaxed security for easy testing
 * - Production: Strict security with JWT, HTTPS, restricted CORS
 * 
 * TODO (Member 3 - Security):
 * 1. Implement JWT authentication:
 *    - Add JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
 *    - Extract username from JWT token
 *    - Validate token signature and expiration
 * 
 * 2. Add JWT handshake interceptor in WebSocketConfig:
 *    - Validate JWT during WebSocket handshake
 *    - Reject connections with invalid/expired tokens
 * 
 * 3. Enable HTTPS:
 *    - Configure SSL/TLS certificate
 *    - Redirect HTTP to HTTPS
 *    - Use secure WebSocket (wss://)
 * 
 * 4. Restrict CORS:
 *    - Change setAllowedOriginPatterns("*") to specific domains
 *    - Configure CORS for REST endpoints
 * 
 * 5. Implement message-level encryption:
 *    - Encrypt message content before sending
 *    - Store encryption keys securely
 *    - Implement key exchange mechanism
 * 
 * Network Security Concepts:
 * - HTTPS: Encrypts data in transit (SSL/TLS)
 * - JWT: Stateless authentication (no server-side session)
 * - CSRF: Protection against Cross-Site Request Forgery
 * - CORS: Control which domains can access the API
 * 
 * @author Team Member 3 - Security
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure HTTP security for the application.
     * 
     * Current Configuration (Development):
     * - Permit all requests (no authentication required)
     * - Disable CSRF for WebSocket endpoints
     * - Allow H2 console in frames
     * 
     * Production Configuration:
     * - Require JWT authentication for API endpoints
     * - Enable CSRF protection for REST endpoints
     * - Restrict frame options
     * - Enable HTTPS
     * 
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Spring Security (Development Mode)");
        
        http
            // Authorize requests
            .authorizeHttpRequests(auth -> auth
                // Permit WebSocket endpoint (for development)
                .requestMatchers("/ws/**").permitAll()
                
                // Permit static resources
                .requestMatchers("/test-client.html", "/favicon.ico", "/css/**", "/js/**").permitAll()
                
                // Permit H2 console (for development)
                .requestMatchers("/h2-console/**").permitAll()
                
                // TODO (Production): Require authentication for API endpoints
                // .requestMatchers("/api/**").authenticated()
                
                // Permit all other requests (for development)
                .anyRequest().permitAll()
            )
            
            // Disable CSRF protection for WebSocket endpoints
            // WebSocket connections use a different security model (token-based)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/ws/**", "/h2-console/**")
                // TODO (Production): Enable CSRF for REST endpoints
                // .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            
            // Allow H2 console to be displayed in frames (for development)
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            );
        
        // TODO (Member 3 - Security): Add JWT authentication filter
        // http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        log.warn("========================================");
        log.warn("SECURITY WARNING: Running in Development Mode");
        log.warn("All endpoints are accessible without authentication");
        log.warn("CSRF protection is disabled for /ws/** and /h2-console/**");
        log.warn("TODO: Enable JWT authentication for production");
        log.warn("TODO: Enable HTTPS for production");
        log.warn("TODO: Restrict CORS origins for production");
        log.warn("========================================");
        
        return http.build();
    }

    // TODO (Member 3 - Security): Implement JWT authentication components
    
    /**
     * Example JWT Authentication Filter (commented out - to be implemented)
     */
    // @Bean
    // public JwtAuthenticationFilter jwtAuthenticationFilter() {
    //     return new JwtAuthenticationFilter(jwtService(), userDetailsService());
    // }
    
    /**
     * Example JWT Service (commented out - to be implemented)
     */
    // @Bean
    // public JwtService jwtService() {
    //     return new JwtService();
    // }
    
    /**
     * Example Password Encoder (commented out - to be implemented)
     */
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
    
    /**
     * Example Authentication Manager (commented out - to be implemented)
     */
    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    //     return config.getAuthenticationManager();
    // }
}
