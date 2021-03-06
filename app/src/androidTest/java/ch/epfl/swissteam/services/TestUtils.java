package ch.epfl.swissteam.services;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ch.epfl.swissteam.services.models.Categories;
import ch.epfl.swissteam.services.models.Post;
import ch.epfl.swissteam.services.models.User;
import ch.epfl.swissteam.services.providers.DBUtility;
import ch.epfl.swissteam.services.utils.Utils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;

public class TestUtils {
    protected static final String M_GOOGLE_ID = "1234";
    protected static final String O_GOOGLE_ID = "456";
    protected static final String URL = TestUtils.getTestUser().getImageUrl_();
    protected static final ArrayList<Categories> CATS = new ArrayList<>(Arrays.asList(Categories.COOKING));
    protected static final User M_USER = new User(M_GOOGLE_ID,"Bear", "polar@north.nth","",
            null,null, null, URL, 0, 0, 0,null,null, true);
    protected static final User O_USER = new User(O_GOOGLE_ID, "Raeb", "hairy@north.nth",
            "", CATS, null, null,  URL, 0, 0, 0,null,null, false);

    protected static User getTestUser(){
        ArrayList<Categories> cat = new ArrayList<>();
        cat.add(Categories.IC);
        HashMap<String, ArrayList<String>> kW = new HashMap<>();
        kW.put(Categories.IC.toString(), new ArrayList<>(Arrays.asList("Python", "Java")));
        User testUser = new User("1234", "testuser", "test@gmail.com",
                "I am a test user", cat, kW, null,
                "https://lh5.googleusercontent.com/-SYTkc6TIZHI/AAAAAAAAAAI/AAAAAAAAABc/EBrA4sSVWQc/photo.jpg",
                0,0, 0,null,null, true);
        return testUser;
    }

    protected static void addTestPost() {
        long timestamp = (new Date()).getTime();
        String key = "1234" + "_" + timestamp;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = cal.getTime();

        DBUtility.get().setPost(new Post(key, "Hello there", "1234",
                "General Kenobi", timestamp, 0, 0, Utils.dateToString(tomorrow)));


    }

    protected static Post getTestPost() {
        long timestamp = (new Date()).getTime();
        String key = "1234" + "_" + timestamp;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = cal.getTime();
        return new Post(key, "Hello there", "1234",
                "General Kenobi", timestamp, 0, 0, Utils.dateToString(tomorrow));
    }

    protected static void sleep(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with "+childPosition+" child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && group.getChildAt(childPosition).equals(view);
            }
        };
    }




    protected static void setMock(){
        DBUtility.get().getDb_().getDatabase().goOffline();
    }

    protected static void recyclerScrollToItemWithTextAndPerformOnItem(int recyclerViewId, String text, ViewAction perform){
        onView(withId(recyclerViewId)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(text)), perform));
    }

    protected static void recyclerScrollToItemWithTextAndPerformClickItem(int recyclerViewId, String text){
        recyclerScrollToItemWithTextAndPerformOnItem(recyclerViewId, text, click());
    }

    protected static ViewAction personalClick(){
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isEnabled(); // no constraints, they are checked above
            }

            @Override
            public String getDescription() {
                return "click plus button";
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.performClick();
            }
        };
    }

    public static Matcher<View> navigationHomeMatcher() {
        return allOf(
                withParent(withClassName(is(Toolbar.class.getName()))),
                withClassName(anyOf(
                        is(ImageButton.class.getName()),
                        is(AppCompatImageButton.class.getName())
                )));
    }




}

