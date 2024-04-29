package shop.bookbom.gateway.security.filter;


import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import shop.bookbom.gateway.common.exception.BaseException;
import shop.bookbom.gateway.common.exception.ErrorCode;
import shop.bookbom.gateway.security.authentication.UserEmailPasswordAuthenticationToken;
import shop.bookbom.gateway.security.jwt.JwtConfig;

/**
 * jwt 토큰을 검증하는 필터
 * -> 토큰
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("now doing jwtAuthenticationFilter");

        String jwt = jwtTokenProvider.resolveToken(request);

        if (!jwtTokenProvider.validateToken(jwt)) // 토큰이 유효한지 검증
            throw new BaseException(ErrorCode.COMMON_ILLEGAL_STATUS);

        Claims claims = jwtTokenProvider.getClaims(jwt);

        GrantedAuthority a = new SimpleGrantedAuthority(claims.get("role", String.class));
        var auth = new UserEmailPasswordAuthenticationToken(
                claims.get("userId", Long.class),
                null,
                List.of(a)
        );

        SecurityContextHolder.getContext()
                .setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/signin");
    }
}
