package shop.bookbom.gateway.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import shop.bookbom.gateway.security.dto.UserEmailPw;
import shop.bookbom.gateway.security.authentication.UserEmailPasswordAuthentication;

/**
 * email과 password를 확인하는 filter
 * 로그인시에만 동작한다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitialAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("now doing InitialAuthenticationFilter");

        ObjectMapper om = new ObjectMapper();
        UserEmailPw userEmailPw = om.readValue(request.getInputStream(), UserEmailPw.class);

        Authentication a = new UserEmailPasswordAuthentication(userEmailPw.getEmail(), userEmailPw.getPw());
        authenticationManager.authenticate(a);
        filterChain.doFilter(request, response);
    }
}
