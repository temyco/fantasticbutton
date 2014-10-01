package stepinmobile.com.testbutton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.stepinmobile.fantasticbutton.api.ButtonHandle;
import com.stepinmobile.fantasticbutton.view.FantasticButton;


public class MyActivity extends Activity {

    private static final String APP_ID_FB = "249969215127359";
    private static final String APP_ID_TWITTER = "y0dBvhvaOvoLwahePtfUq3XXR";
    private static final String APP_SECRET_TWITTER = "1elVmcRODOgGAjy8mgtlumKRO3P2upZFeQzWwtjCSxyZ7YDv8y";

    private ButtonHandle handle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        handle = ButtonHandle.getInstance(this);
        handle.setFbAppId(APP_ID_FB);
        handle.setTwitterAppId(APP_ID_TWITTER);
        handle.setTwitterAppSecret(APP_SECRET_TWITTER);

        FantasticButton btn = (FantasticButton) findViewById(R.id.btn_fantastic);
        btn.setTextForShare("Fantastic button will care about your shares");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == handle.getSso_code()) {
            if(handle != null){
                handle.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

}
