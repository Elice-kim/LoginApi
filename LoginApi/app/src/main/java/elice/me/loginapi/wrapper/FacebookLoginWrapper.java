package elice.me.loginapi.wrapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

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

public class FacebookLoginWrapper implements FacebookCallback<LoginResult>, SnsWrapper {

    private CallbackManager mCallbackManager = CallbackManager.Factory.create();
    private String userId;
    private String email;
    private String nick;
    private LoginSuccessCallback callback;

    public FacebookLoginWrapper(LoginSuccessCallback callback) {
        this.callback = callback;
    }

    public void onCreate() {
        LoginManager.getInstance().registerCallback(mCallbackManager, this);
    }

    @Override
    public void open(FragmentActivity activity) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            requestUserInfo();
            Log.e("login", "call");
        } else {
            LoginManager.getInstance()
                    .logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
            Log.e("request", "call");
        }
    }

    public void onDestroy() {
        LoginManager.getInstance().unregisterCallback(mCallbackManager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        requestUserInfo();
        Log.e("loginsuccess", "call");

    }

    @Override
    public void onCancel() {
        callback.loginFail("Login Canceled");
    }

    @Override
    public void onError(FacebookException error) {
        callback.loginFail(error.getMessage());
    }

    public void requestUserInfo() {
        final LoginSuccessCallback loginCallback = callback;
        AccessToken token = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null) {
                            loginCallback.loginFail(response.getError().getException().getMessage());
                        } else {
                            userId = AccessToken.getCurrentAccessToken().getUserId();
                            try {
                                email = object.getString("email");
                                nick = object.getString("name");
                                loginCallback.loginSuccess(userId, email, nick, "");
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
