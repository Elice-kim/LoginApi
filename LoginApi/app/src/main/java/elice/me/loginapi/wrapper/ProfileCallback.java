package elice.me.loginapi.wrapper;

/**
 * Created by elice.kim on 17/08/2017.
 */

public interface ProfileCallback {

    void onSuccess(String userId, String email, String nick);
    void onFailed(String errorMessage);
}
