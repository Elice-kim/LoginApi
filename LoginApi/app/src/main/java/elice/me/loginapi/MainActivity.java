package elice.me.loginapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import elice.me.loginapi.wrapper.FacebookLoginWrapper;
import elice.me.loginapi.wrapper.GoogleLoginWrapper;
import elice.me.loginapi.wrapper.LoginSuccessCallback;

public class MainActivity extends AppCompatActivity implements LoginSuccessCallback {

    public static final int RC_SIGN_IN = 100;

    @BindView(R.id.googleLoginBtn)
    SignInButton googleSignBtn;
    @BindView(R.id.facebookLoginBtn)
    LoginButton facebookLoginBtn;
    @BindView(R.id.googleLogoutBtn)
    Button googleLogoutBtn;
    @BindView(R.id.signin_fb)
    TextView signInFb;
    @BindView(R.id.signin_gp)
    TextView singInGp;

    private FacebookLoginWrapper facebookLoginWrapper;
    private GoogleLoginWrapper googleLoginWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        googleLoginWrapper = new GoogleLoginWrapper(MainActivity.this, this);
        facebookLoginWrapper = new FacebookLoginWrapper(this);
        googleLogoutBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        facebookLoginWrapper.onDestroy();
    }

    @OnClick(R.id.signin_fb)
    void facebookClick(){
        facebookLoginWrapper.onCreate();
        facebookLoginWrapper.open(MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            googleLoginWrapper.onActivityResult(requestCode, resultCode, data);
            return;
        }
        facebookLoginWrapper.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.signin_gp)
    void googleClicked(){
        googleLoginWrapper.open(MainActivity.this);
    }

//    @OnClick(R.id.googleLogoutBtn)
//    void googleLogoutClicked() {
//        googleLoginWrapper.googleLogoutClicked(this);
//    }

//    @Override
//    public void googleLogoutSuccess() {
//        Toast.makeText(MainActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
//        googleLogoutBtn.setVisibility(View.GONE);
//        googleSignBtn.setVisibility(View.VISIBLE);
//    }

    @Override
    public void loginSuccess(String userId, String email, String nick, String value) {
        Log.e("get profile success", "call");
        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("id", userId);
        intent.putExtra("name", nick);
        intent.putExtra("token", value);
        startActivity(intent);
    }

    @Override
    public void loginFail(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
