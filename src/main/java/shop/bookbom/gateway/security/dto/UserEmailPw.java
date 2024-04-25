package shop.bookbom.gateway.security.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserEmailPw {
    String email;
    String pw;
}
