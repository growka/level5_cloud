package common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter

public class AuthMessage extends Message {

    private String username;
    private String password;
    private boolean result;
    private String folder;
    private String token;
    private String srvMsg;
    private boolean registration;
    private int userId;

    @Override
    public String toString() {
        return "{ Username " + username +
                ", password " + password +
                ", result " + result +
                ", folder " + folder +
                ", token " + token +
                ", srvMsg " + srvMsg +
                ", registration " + registration
                ;
    }
}
