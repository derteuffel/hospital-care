package com.hospital.security;

import com.hospital.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
@Order(2)
public class PatientSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CompteService compteService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .antMatcher("/patient/**").authorizeRequests()
                .antMatchers("/downloadFile/**","/static/**","/patient/**").permitAll()
                .antMatchers("/patient/**").access("hasAnyRole('ROLE_PATIENT','ROLE_ROOT','ROLE_DOCTOR','ROLE_INFIRMIER')")
                .and()
                .formLogin()
                .loginPage("/patient/login")
                .loginProcessingUrl("/patient/login")
                .defaultSuccessUrl("/patient/home")
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/patient/logout"))
                .logoutSuccessUrl("/patient/login?logout")
                .and()
                .exceptionHandling().accessDeniedPage("/patient/access-denied");

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/","/registration**",
                        "/fonts/**",
                        "/assets/**",
                        "/css/**",
                        "/img/**",
                        "/js/**",
                        "/downloadFile/**",
                        "/scss/**"
                );
    }

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }
}
