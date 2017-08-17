package elice.me.loginapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 100;

    @BindView(R.id.googleLoginBtn)
    SignInButton googleSignBtn;
    @BindView(R.id.facebookLoginBtn)
    LoginButton facebookLoginBtn;
    @BindView(R.id.googleLogoutBtn)
    Button googleLogoutBtn;

    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    private String name;
    private String email;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mGoogleApiClient = makeGoogleApiClient(this);
        callbackManager = CallbackManager.Factory.create();
        facebookLoginClicked();
        googleLogoutBtn.setVisibility(View.GONE);
    }

    private void facebookLoginClicked() {
        facebookLoginBtn.setReadPermissions("public_profile", "email");

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    Intent intent = new Intent(MainActivity.this,
                                            UserInfoActivity.class);
                                    intent.putExtra("name", object.getString("name"));
                                    intent.putExtra("email", object.getString("email"));
                                    intent.putExtra("id", object.getString("id"));
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Facebook 로그인 취소", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private GoogleApiClient makeGoogleApiClient(FragmentActivity fragmentActivity) {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        return new GoogleApiClient.Builder(this)
                .enableAutoManage(fragmentActivity, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @OnClick(R.id.googleLoginBtn)
    void googleLoginClicked() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("sign", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();

            Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
            intent.putExtra("email", account.getEmail());
            intent.putExtra("id", account.getId());
            intent.putExtra("name", account.getDisplayName());
            startActivity(intent);

            googleSignBtn.setVisibility(View.GONE);
            googleLogoutBtn.setVisibility(View.VISIBLE);
            googleLogoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    Toast.makeText(MainActivity.this, "로그아웃 성공",
                                            Toast.LENGTH_SHORT).show();
                                    googleLogoutBtn.setVisibility(View.GONE);
                                    googleSignBtn.setVisibility(View.VISIBLE);
                                }
                            });
                }
            });
        } else {
            Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show();
        }

    }
}
