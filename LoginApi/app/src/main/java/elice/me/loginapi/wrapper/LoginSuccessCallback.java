package elice.me.loginapi.wrapper;

/**
 * Created by ladmusician.kim on 18/08/2017.
 */

public interface LoginSuccessCallback {

    void loginSuccess(String userId, String email, String nick, String value);
    void loginFail(String errorMessage);
}
