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
import org.springframework.web.filter.OncePerRequestFilter;
import shop.bookbom.gateway.security.authentication.UserEmailPasswordAuthenticationToken;
import shop.bookbom.gateway.security.dto.UserEmailPw;

/**
 * email과 password를 확인하는 filter
 * 로그인시에만 동작한다.
 */
@Slf4j
@RequiredArgsConstructor
public class InitialAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;



    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("now doing InitialAuthenticationFilter");

        ObjectMapper om = new ObjectMapper();
        UserEmailPw userEmailPw = om.readValue(request.getInputStream(), UserEmailPw.class);

        Authentication a = new UserEmailPasswordAuthenticationToken(userEmailPw.getEmail(), userEmailPw.getPw());
        authenticationManager.authenticate(a);

        request.setAttribute("userId", a.getName());
        request.setAttribute("role", a.getAuthorities());
        filterChain.doFilter(request, response);
    }


//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
//            throws AuthenticationException {
//        log.debug("now doing InitialAuthenticationFilter");
//
//        Authentication authentication;
//
//        try{
//            authentication = getUserIdRole(request);
//        } catch (Exception e) {
//            log.error("InitialAuthenticationFilter throws exception");
//            throw new RuntimeException(e);
//        }
//
//        return authenticationManager.authenticate(authentication);
//    }
//

}
