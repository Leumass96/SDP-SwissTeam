package ch.epfl.swissteam.services;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.swissteam.services.models.Categories;
import ch.epfl.swissteam.services.models.User;
import ch.epfl.swissteam.services.providers.DBUtility;
import ch.epfl.swissteam.services.view.activities.MainActivity;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DBUtilityTest extends SocializeTest<MainActivity> {

    User testUser;

    public DBUtilityTest(){
        setTestRule(MainActivity.class);
    }

    @Override
    public void initialize() {
        testUser = TestUtils.getTestUser();
        testUser.addToDB(DBUtility.get().getDb_());
    }

    @Test
    public void testSingleton() {
        assertEquals(DBUtility.get(), DBUtility.get());
    }

    @Test
    public void getCatTest(){
        DBUtility.get().getCategory(Categories.fromString("Computer"), c->{

        });
    }

    @Test
    public void getUserfromCatTest(){
        DBUtility.get().getUsersFromCategory(Categories.COOKING, c->{

        });
        DBUtility.get().getUsersFromCategory(Categories.ALL,c->{

        });
    }

    @Test
    public void testNotifyNewMessages() {
        DBUtility.get().notifyNewMessages(testRule_.getActivity(), testUser.getGoogleId_());
    }
}
