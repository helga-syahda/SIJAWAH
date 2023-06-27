package propensi.SIJAWAH.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/login-sso", "/validate-ticket").permitAll()
                .antMatchers("/user/profil").hasAnyAuthority("admin", "ceo", "cs", "socmed", "finance")
                .antMatchers("/user/ubah-password").hasAnyAuthority("admin", "ceo", "cs", "socmed", "finance")
                .antMatchers("/user/*").hasAnyAuthority("admin")
                .antMatchers("/rapat/*").hasAnyAuthority("cs", "ceo", "socmed", "finance")
                .antMatchers("/rapat/update/*").hasAnyAuthority("ceo")
                .antMatchers("/rapat/ajuan/delete/*").hasAnyAuthority("cs", "socmed", "finance")
                .antMatchers("/pelanggan/*").hasAnyAuthority("cs", "ceo")
                .antMatchers("/pekerjaan/add").hasAnyAuthority("ceo")
                .antMatchers("/pekerjaan/view-all").hasAnyAuthority("cs", "ceo", "socmed", "finance")
                .antMatchers("/chart/*").hasAnyAuthority( "ceo", "finance")
                .antMatchers("/chart").hasAnyAuthority( "ceo", "finance")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/")
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login").permitAll();
        return http.build();
    }


    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(encoder())
                .withUser("adminsijawah")
                .password(encoder().encode("admin123"))
                .authorities("admin"); // Default Admin
    }

    @Autowired
    private UserDetailsServiceImpl userDetailService;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(encoder());
    }
}
