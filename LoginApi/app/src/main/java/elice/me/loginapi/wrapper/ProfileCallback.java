package elice.me.loginapi.wrapper;

import com.facebook.FacebookException;

/**
 * Created by elice.kim on 17/08/2017.
 */

public interface ProfileCallback {

    void onSuccess(String userId, String email, String nick);
    void onFailed(FacebookException exception);
}
