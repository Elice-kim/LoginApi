package elice.me.loginapi.wrapper;

/**
 * Created by elice.kim on 18/08/2017.
 */

public interface GoogleSignInCallback {

    void googleLoginSuccess(String userId, String email, String nick);
    void googleLoginFail();
}