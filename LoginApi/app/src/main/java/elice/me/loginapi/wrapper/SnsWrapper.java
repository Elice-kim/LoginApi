package elice.me.loginapi.wrapper;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

/**
 * Created by ladmusician.kim on 18/08/2017.
 */

interface SnsWrapper {

    void open(FragmentActivity activity);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
