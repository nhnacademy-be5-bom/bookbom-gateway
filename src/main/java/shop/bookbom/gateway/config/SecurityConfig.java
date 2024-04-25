package shop.bookbom.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shop.bookbom.gateway.security.filter.InitialAuthenticationFilter;
import shop.bookbom.gateway.security.filter.JwtAuthenticationFilter;
import shop.bookbom.gateway.security.filter.UserIdRoleFilter;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    @Autowired
    private InitialAuthenticationFilter initialAuthenticationFilter;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserIdRoleFilter userIdRoleFilter;

    /**  WebSecurityCustomizer
     *  권한 확인이 필요 없는 api 요청 url을 추가
     *  차후 수정
     */
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring()
//                .antMatchers("/shop/members");
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.httpBasic().disable();
        log.debug("http basic disable");
        http.formLogin().disable();
        log.debug("form login disable");
        http.csrf().disable();
        log.debug("csrf disable");
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .authorizeRequests()
                .antMatchers("/auth/token").authenticated()
                .and()
                .addFilterBefore(initialAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(userIdRoleFilter, InitialAuthenticationFilter.class);

        http
                .antMatcher("/shop/**")
                .addFilterAfter(jwtAuthenticationFilter, InitialAuthenticationFilter .class); // or any other filter

        http
                .authorizeRequests()
                .antMatchers("/admin/**").hasAnyRole("ROLE_ADMIN")
                .antMatchers("/open/**").permitAll()
                .anyRequest().authenticated();

        return http.build();
    }
}
