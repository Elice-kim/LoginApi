package elice.me.loginapi.wrapper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created by elice.kim on 17/08/2017.
 */

public class FacebookLoginWrapper implements FacebookCallback<LoginResult> {

    private CallbackManager mCallbackManager = CallbackManager.Factory.create();
    private String userId;
    private String email;
    private String nick;
    private TokenCallback callback;

    public FacebookLoginWrapper(TokenCallback callback) {
        this.callback = callback;
    }

    public void open(Activity activity) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            callback.onSuccess(accessToken.getToken());
        } else {
            LoginManager.getInstance()
                    .logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
        }
    }

    public void onCreate() {
        LoginManager.getInstance().registerCallback(mCallbackManager, this);
    }

    public void onDestroy() {
        LoginManager.getInstance().unregisterCallback(mCallbackManager);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        callback.onSuccess(loginResult.getAccessToken().getToken());

    }

    @Override
    public void onCancel() {
        callback.onCancel();
    }

    @Override
    public void onError(FacebookException error) {
        callback.onError(error.getMessage());
    }

    public void requestUser(final ProfileCallback profileCallback) {
        AccessToken token = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null) {
                            profileCallback.onFailed(response.getError().getException().getMessage());
                        } else {
                            userId = AccessToken.getCurrentAccessToken().getUserId();
                            try {
                                email = object.getString("email");
                                nick = object.getString("name");
                                profileCallback.onSuccess(userId, email, nick);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,name");
        parameters.putString("locale", Locale.getDefault().getLanguage());
        request.setParameters(parameters);
        request.executeAsync();
    }
}
