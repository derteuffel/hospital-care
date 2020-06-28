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
@Order(6)
public class DirecteurSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CompteService compteService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .antMatcher("/directeur/**").authorizeRequests()
                .antMatchers("/downloadFile/**","/static/**").permitAll()
                .antMatchers("/directeur/**").access("hasAnyRole('ROLE_DIRECTEUR','ROLE_ROOT')")
                .and()
                .formLogin()
                .loginPage("/directeur/login")
                .loginProcessingUrl("/directeur/login")
                .defaultSuccessUrl("/directeur/home")
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/directeur/logout"))
                .logoutSuccessUrl("/directeur/login?logout")
                .and()
                .exceptionHandling().accessDeniedPage("/directeur/access-denied");

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
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

}
