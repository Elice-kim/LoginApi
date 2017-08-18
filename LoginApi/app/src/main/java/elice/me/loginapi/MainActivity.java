package elice.me.loginapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import elice.me.loginapi.wrapper.FacebookLoginWrapper;
import elice.me.loginapi.wrapper.GoogleLoginWrapper;
import elice.me.loginapi.wrapper.GoogleLogoutCallback;
import elice.me.loginapi.wrapper.GoogleSignInCallback;
import elice.me.loginapi.wrapper.ProfileCallback;
import elice.me.loginapi.wrapper.TokenCallback;

public class MainActivity extends AppCompatActivity
        implements TokenCallback, ProfileCallback, GoogleSignInCallback, GoogleLogoutCallback {

    public static final int RC_SIGN_IN = 100;

    @BindView(R.id.googleLoginBtn)
    SignInButton googleSignBtn;
    @BindView(R.id.facebookLoginBtn)
    LoginButton facebookLoginBtn;
    @BindView(R.id.googleLogoutBtn)
    Button googleLogoutBtn;

    private FacebookLoginWrapper facebookLoginWrapper;
    private GoogleLoginWrapper loginWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loginWrapper = new GoogleLoginWrapper(MainActivity.this, this);
        facebookLoginWrapper = new FacebookLoginWrapper(this);
        googleLogoutBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        facebookLoginWrapper.onDestroy();
    }

    @OnClick(R.id.facebookLoginBtn)
    void facebookLoginClick() {
        facebookLoginWrapper.onCreate();
    }

    @Override
    public void onSuccess(String token) {
        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();
        facebookLoginWrapper.requestUser(this);
    }

    @Override
    public void onCancel() {
        Toast.makeText(MainActivity.this, "Facebook 로그인 취소", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(String userId, String email, String nick) {
        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
        intent.putExtra("name", nick);
        intent.putExtra("email", email);
        intent.putExtra("id", userId);
        startActivity(intent);
    }

    @Override
    public void onFailed(String errorMessage) {
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            loginWrapper.onActivityResult(data);
            return;
        }
        facebookLoginWrapper.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.googleLoginBtn)
    void googleLoginClicked() {
        loginWrapper.signIn();
    }

    @Override
    public void googleLoginSuccess(String userId, String email, String nick) {
        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("id", userId);
        intent.putExtra("name", nick);
        startActivity(intent);
        googleSignBtn.setVisibility(View.GONE);
        googleLogoutBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void googleLoginFail() {
        Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.googleLogoutBtn)
    void googleLogoutClicked() {
        loginWrapper.googleLogoutClicked(this);
    }

    @Override
    public void googleLogoutSuccess() {
        Toast.makeText(MainActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
        googleLogoutBtn.setVisibility(View.GONE);
        googleSignBtn.setVisibility(View.VISIBLE);
    }
}
