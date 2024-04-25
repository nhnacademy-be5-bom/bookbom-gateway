package shop.bookbom.gateway.security.authentication;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UserEmailPasswordAuthentication extends UsernamePasswordAuthenticationToken {

    public UserEmailPasswordAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public UserEmailPasswordAuthentication(Object principal, Object credentials,
                                           Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
