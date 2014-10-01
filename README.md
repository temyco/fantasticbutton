FantasticButton Android library
=====

FantasticButton is an apk-library for Android application projects.

It provides a simple in use button that supports:
- Facebook login and sharing
- Twitter login and sharing
- Google+ login and sharing
- Rating the app directly on GooglePlay
- Nice unobtrusive button animation

![](https://dl.dropboxusercontent.com/u/18433348/fantastic_btn_1.png "Button")      ![](https://dl.dropboxusercontent.com/u/18433348/fantastic_btn_dlg.png "Sharing dialog")

# How to use?

- Download fantastic_button_module
- Open your project in Android Studio and use File -> Import Module command
- Open Project Structure (Ctrl + Alt + Shift + S)
- Choose your app under the Modules menu
- Go to Dependencies and add fantastic_button_module as a Module dependency

- Go to your layout file and add the following code. You can choose any drawable you want.
The library includes several colorful heart-shape selectors under the /drawable folder.

  * By default button is animated. If you want you button not to be animated you should provide the ```is_animated="boolean"``` attribute in your xml. 
The animation was fetch from the G+ [post](https://plus.google.com/+CyrilMottier/posts/FABaJhRMCuy).

  * A default delay between the animations is 5 sec. but you can change it by providing
the ```anim_delay_in_millis="int"``` attribute in the xml.

  * Also you can decide not to use rate on GooglePlay function. In this case you should
provide ``` is_google_play_enabled="boolean" ``` attribute.

```android
<com.stepinmobile.fantasticbutton.view.FantasticButton
        xmlns:custom="http://schemas.android.com/apk/res/stepinmobile.com.testbutton"
        android:id="@+id/btn_fantastic_green"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/green_heart_selector"
        custom:is_animated="true"
        custom:is_google_play_enabled="true"
        custom:anim_delay_in_millis="5000" />
```

- To finish the button's setup you should go to your Activity and add this code. Note that
the requestCode for Facebook SSO login is ``` ACTIVITY_SSO = 1000.```

```android
	private ButtonHandle handle;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	...
		handle = ButtonHandle.getInstance(this);
        handle.setFbAppId(APP_ID_FB);
        handle.setTwitterAppId(APP_ID_TWITTER);
        handle.setTwitterAppSecret(APP_SECRET_TWITTER);

        FantasticButton btn = (FantasticButton) findViewById(R.id.btn_fantastic);
        btn.setTextForShare("Fantastic button will care about your shares");
        ...
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == handle.getSso_code()) {
            if(handle != null){
                handle.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
```
The full example is available in the app module of this project.

LICENSE

Except as otherwise noted, the Facebook SDK for Android is licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html).

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
