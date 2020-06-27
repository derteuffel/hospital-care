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
@Order(4)
public class DoctorSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CompteService compteService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .antMatcher("/doctor/**").authorizeRequests()
                .antMatchers("/downloadFile/**","/static/**").permitAll()
                .antMatchers("/doctor/**").access("hasAnyRole('ROLE_DOCTOR','ROLE_ROOT')")
                .and()
                .formLogin()
                .loginPage("/doctor/login")
                .loginProcessingUrl("/doctor/login")
                .defaultSuccessUrl("/doctor/home")
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/doctor/logout"))
                .logoutSuccessUrl("/doctor/login?logout")
                .and()
                .exceptionHandling().accessDeniedPage("/doctor/access-denied");

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

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(compteService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
}
