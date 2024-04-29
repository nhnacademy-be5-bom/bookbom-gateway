package shop.bookbom.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import shop.bookbom.gateway.security.filter.InitialAuthenticationFilter;
import shop.bookbom.gateway.security.filter.JwtAuthenticationFilter;
import shop.bookbom.gateway.security.filter.UserIdRoleFilter;
import shop.bookbom.gateway.security.jwt.JwtConfig;
import shop.bookbom.gateway.security.provider.UserEmailPasswordAuthenticationProvider;
import shop.bookbom.gateway.security.proxy.AuthServerProxy;

@Slf4j
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig  {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserEmailPasswordAuthenticationProvider userEmailPasswordAuthenticationProvider(){
        return new UserEmailPasswordAuthenticationProvider();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(userEmailPasswordAuthenticationProvider());
    }
    @Bean
    public InitialAuthenticationFilter initialAuthenticationFilter() {
        InitialAuthenticationFilter initialAuthenticationFilter
                = new InitialAuthenticationFilter(authenticationManager());
        return initialAuthenticationFilter;
    }

    @Bean
    public AuthServerProxy authServerProxy() {
        return new AuthServerProxy();
    }

    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtConfig());
    };

    @Bean
    public UserIdRoleFilter userIdRoleFilter() {
        return new UserIdRoleFilter(authServerProxy());
    };

    /**  WebSecurityCustomizer
     *  권한 확인이 필요 없는 api 요청 url을 추가
     *  차후 수정
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/shop/members/signup")
                .antMatchers("/auth/token")
                .antMatchers("/open/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic().disable()
                .formLogin().disable()
//
                .csrf().disable()
                .cors().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .authorizeHttpRequests(
                        (authz) -> authz
                                .antMatchers("/auth/token").permitAll()
                                .and()
                                .addFilterBefore(userIdRoleFilter(), UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(initialAuthenticationFilter(), UserIdRoleFilter.class)
                );

        http
                .authorizeHttpRequests(
                        (authz) -> authz
                                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                                .antMatchers("/shop/**").hasAnyRole("USER")
                                .antMatchers("/open/**").permitAll()
                );
        http
                .authorizeHttpRequests(
                        (authz) -> authz
                                .and()
                                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                );

        return http.build();
    }
}
