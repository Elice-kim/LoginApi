package elice.me.loginapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserInfoActivity extends AppCompatActivity {

    @BindView(R.id.emailTextView)
    TextView emailTextView;
    @BindView(R.id.idTextView)
    TextView idTextView;
    @BindView(R.id.nameTextView)
    TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ButterKnife.bind(this);
        Intent intent = getIntent();

        emailTextView.setText("Email : " + intent.getStringExtra("email"));
        idTextView.setText("Id : " +intent.getStringExtra("id"));
        nameTextView.setText("Name : " +intent.getStringExtra("name"));
    }

}
