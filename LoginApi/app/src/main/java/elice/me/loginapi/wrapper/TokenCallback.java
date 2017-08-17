package elice.me.loginapi.wrapper;

import com.facebook.FacebookException;

/**
 * Created by elice.kim on 17/08/2017.
 */

public interface TokenCallback{

    void onSuccess(String token);
    void onCancel();
    void onError(FacebookException error);
}
