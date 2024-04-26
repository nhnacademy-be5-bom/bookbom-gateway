package shop.bookbom.gateway.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import shop.bookbom.gateway.security.dto.UserEmailPw;
import shop.bookbom.gateway.security.dto.UserIdRole;
import shop.bookbom.gateway.security.proxy.AuthServerProxy;

/**
 * InitialAuthenticationFilter 후에 동작하는 filter.
 * email을 통해 role과 id값을 받아온다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserIdRoleFilter extends OncePerRequestFilter {
    @Autowired
    private final AuthServerProxy authServerProxy;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        ObjectMapper om = new ObjectMapper();
        UserEmailPw userEmailPw = om.readValue(request.getInputStream(), UserEmailPw.class);

        UserIdRole userIdRole = authServerProxy.getUserIdRole(userEmailPw.getEmail());
        request.setAttribute("userId", userIdRole.getUserId());
        request.setAttribute("role", userIdRole.getRole());

        log.debug("now doing UserIdRoleFilter. now request is : " + request.toString());
        filterChain.doFilter(request, response);
    }
}
