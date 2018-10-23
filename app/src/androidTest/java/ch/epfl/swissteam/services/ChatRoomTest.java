package ch.epfl.swissteam.services;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.swissteam.services.UtilityTest.nthChildOf;

public class ChatRoomTest extends FirebaseTest{

    private static final String mGoogleId = "1234";
    private static final String oGoogleId = "5678";
    private static final String url = TestUtils.getATestUser().getImageUrl_();
    private static final ArrayList<Categories> cats = new ArrayList<>(Arrays.asList(Categories.COOKING));
    private static final User mUser = new User(mGoogleId,"Bear", "polar@north.nth","",null,url);
    private static final User oUser = new User(oGoogleId, "Raeb", "hairy@north.nth", "", cats, url);

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);
    /*public final IntentsTestRule<ChatRoom> mActivityRule =
            new IntentsTestRule<ChatRoom>(ChatRoom.class){
                @Override
                protected Intent getActivityIntent() {
                    //added predefined intent data//
                    Intent intent = new Intent();
                    intent.putExtra(NewProfileDetails.GOOGLE_ID_TAG, oGoogleId);
                    return intent;
                }
            };*/

    @Override
    public void initialize(){
        GoogleSignInSingleton.putUniqueID(mGoogleId);
        oUser.addToDB(FirebaseDatabase.getInstance().getReference());
        mUser.addToDB(FirebaseDatabase.getInstance().getReference());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.button_maindrawer_services));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText(oUser.getName_())).perform(click());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.button_profile_toChat)).perform(click());
    }

    @Test
    public void sendMessageWorksWithNonEmpty() {
        String text = "Le roi est mort ! Vive le roi !";
        onView(withId(R.id.message_input)).perform(typeText(text)).check(matches(withText(text)));
        onView(withId(R.id.message_send_button)).perform(click());
        onView(withId(R.id.recycler_view_message)).check(matches(hasDescendant(withText(text))));
    }

    /* Examples
        - onView(nthChildOf(withId(R.id.recycleview), 0).check(matches(hasDescendant(withText("Some text"))))
        - onView(withId(R.id.recycleview)).check(matches(hasDescendant(withText("Some text"))))
     */
}
