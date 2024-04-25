package shop.bookbom.gateway.common;

import lombok.Builder;
import lombok.Data;

@Data
public class ResponseHeader {
    private boolean isSuccessful;
    private int resultCode;
    private String resultMessage;

    @Builder
    public ResponseHeader(boolean isSuccessful, int resultCode, String resultMessage) {
        this.isSuccessful = isSuccessful;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
}
