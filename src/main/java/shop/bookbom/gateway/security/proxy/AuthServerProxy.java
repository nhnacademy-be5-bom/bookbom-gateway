package shop.bookbom.gateway.security.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import shop.bookbom.gateway.common.CommonResponse;
import shop.bookbom.gateway.common.exception.BaseException;
import shop.bookbom.gateway.common.exception.ErrorCode;
import shop.bookbom.gateway.security.dto.UserEmailPw;
import shop.bookbom.gateway.security.dto.UserIdRole;

/**
 * shop 서버와 통신하여 필요한 정보를 받아옴
 * checkUser : email, password를 통해 올바른 사용자인지 확인
 * getUserIdRole : email값을 통해 id와 role을 받아옴
 */
@Slf4j
@Component
public class AuthServerProxy {

    String baseUrl = "";

    @Autowired
    private RestTemplate rest;

    public boolean checkUser(String email, String password) {
        String url = baseUrl = "/shop/check";

        var body = UserEmailPw.builder()
                        .email(email)
                        .pw(password)
                        .build();

        var request = new HttpEntity<>(body);
        CommonResponse<Boolean> response = rest.postForObject(url, request, CommonResponse.class);

        log.info(String.valueOf(response.getHeader().getResultCode()));

        if (response.getHeader().getResultCode() != 200 || !response.getResult().booleanValue()) {
            throw new BaseException(ErrorCode.COMMON_INVALID_PARAMETER);
        }
        return response.getResult().booleanValue();
    }

    public UserIdRole getUserIdRole(String email) {
        String url = baseUrl = "/shop/check";

        var request = new HttpEntity<>(email);
        CommonResponse<UserIdRole> response = rest.postForObject(url, request, CommonResponse.class);
        log.info(String.valueOf(response.getHeader().getResultCode()));

        if (response.getHeader().getResultCode() != 200 || response.getResult().getRole().isBlank()) {
            throw new BaseException(ErrorCode.COMMON_INVALID_PARAMETER);
        }
        return response.getResult();
    }
}
