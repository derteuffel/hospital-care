package com.hospital.security;


import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CompteService compteService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/hospital-care/**").authorizeRequests()
                .antMatchers("/hospital-care/**").access("hasAnyRole('ROLE_ROOT','ROLE_COORDINATOR')")
                .and()
                .formLogin()
                .loginPage("/hospital-care/login")
                .loginProcessingUrl("/hospital-care/login/process")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .and()
                .exceptionHandling().accessDeniedPage("/access-denied");
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/js/**",
                        "/css/**",
                        "/images/**",
                        "/vendor/**",
                        "/downloadFile/**",
                        "/fonts**",
                        "/coordinator/kidole/registration",
                        "/static/**"
                );
    }
}
