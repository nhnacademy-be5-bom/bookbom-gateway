package shop.bookbom.gateway.security.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import shop.bookbom.gateway.common.exception.BaseException;
import shop.bookbom.gateway.common.exception.ErrorCode;
import shop.bookbom.gateway.security.authentication.UserEmailPasswordAuthentication;
import shop.bookbom.gateway.security.proxy.AuthServerProxy;

/**
 * user의 email과 password를 proxy를 통해 확인.
 * proxy를 통해 받은 값이 true이면 authentication을 return
 */
@Component
public class UserEmailPasswordAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthServerProxy authServerProxy;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = passwordEncoder.encode(String.valueOf(authentication.getCredentials()) + "bom" + authentication.getName());

        boolean result;
        try {
            result = authServerProxy.checkUser(email,password);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.COMMON_INVALID_PARAMETER);
        }
        if (result)
            return new UsernamePasswordAuthenticationToken(email,password);
        else
            throw new BaseException(ErrorCode.COMMON_INVALID_PARAMETER);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserEmailPasswordAuthentication.class.isAssignableFrom(authentication);
    }
}
