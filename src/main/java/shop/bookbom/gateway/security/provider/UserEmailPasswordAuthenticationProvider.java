package shop.bookbom.gateway.security.provider;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import shop.bookbom.gateway.common.exception.BaseException;
import shop.bookbom.gateway.common.exception.ErrorCode;
import shop.bookbom.gateway.security.authentication.UserEmailPasswordAuthenticationToken;
import shop.bookbom.gateway.security.authentication.UserIdPasswordAuthenticationToken;
import shop.bookbom.gateway.security.dto.UserIdRole;
import shop.bookbom.gateway.security.proxy.AuthServerProxy;

/**
 * user의 email과 password를 proxy를 통해 확인.
 * proxy를 통해 받은 값이 true이면 authentication을 return
 */
@Slf4j
public class UserEmailPasswordAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthServerProxy authServerProxy;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = passwordEncoder.encode(String.valueOf(authentication.getCredentials()) + "bom" + authentication.getName());

        boolean result = authServerProxy.checkUser(email,password);

        if (result)
            return getUserIdRole(new UsernamePasswordAuthenticationToken(email,password));
        else
            throw new BaseException(ErrorCode.COMMON_INVALID_PARAMETER);
    }


    public Authentication getUserIdRole(Authentication authentication) {

        UserIdRole userIdRole = authServerProxy.getUserIdRole(authentication.getName());

        log.debug("now doing UserIdRole. now request is : " + userIdRole.getUserId());

        return new UserIdPasswordAuthenticationToken(userIdRole.getUserId(), null,
                Collections.singleton(new SimpleGrantedAuthority(
                        userIdRole.getRole())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserEmailPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


}
