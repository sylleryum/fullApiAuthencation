package com.sylleryum.sylleryum.config.security;

import com.sylleryum.sylleryum.config.ResponseTokenBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final JWTKey jwtKey;
    private final ResponseTokenBuilder responseTokenBuilder;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder, JWTKey jwtKey, ResponseTokenBuilder responseTokenBuilder) {
        this.passwordEncoder = passwordEncoder;
        this.jwtKey = jwtKey;
        this.responseTokenBuilder = responseTokenBuilder;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
            }
        };
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), responseTokenBuilder))
                .addFilterAfter(new TokenFilter(jwtKey),JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/v1/register", "/v1/refresh", "/v1/confirm", "/v1/reset").permitAll()
                //.antMatchers("/v1/**").hasAnyRole("ADMIN")
                .anyRequest()
                .authenticated();
    }
}
