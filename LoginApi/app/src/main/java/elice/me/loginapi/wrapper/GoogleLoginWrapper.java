package elice.me.loginapi.wrapper;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import static elice.me.loginapi.MainActivity.RC_SIGN_IN;

/**
 * Created by elice.kim on 18/08/2017.
 */

public class GoogleLoginWrapper implements SnsWrapper {

    private GoogleApiClient googleApiClient;
    private LoginSuccessCallback googleLoginCallback;

    public GoogleLoginWrapper(FragmentActivity fragmentActivity, LoginSuccessCallback callback) {
        this.googleApiClient = makeGoogleApiClient(fragmentActivity);
        this.googleLoginCallback = callback;
    }

    private GoogleApiClient makeGoogleApiClient(FragmentActivity fragmentActivity) {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        return new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void open(FragmentActivity fragmentActivity) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        fragmentActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("sign", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            googleLoginCallback.loginSuccess(account.getId(),
                    account.getEmail(),
                    account.getDisplayName(), account.getIdToken());
        } else {
            googleLoginCallback.loginFail("login fail");
        }
    }

}

