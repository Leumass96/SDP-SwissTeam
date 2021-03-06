package ch.epfl.swissteam.services;

import org.junit.Test;

import ch.epfl.swissteam.services.models.User;
import ch.epfl.swissteam.services.providers.DBUtility;
import ch.epfl.swissteam.services.providers.GoogleSignInSingleton;
import ch.epfl.swissteam.services.view.activities.MainActivity;

import static org.junit.Assert.assertEquals;

public class GoogleSignInSingletonTest extends SocializeTest<MainActivity> {

    private User testUser = TestUtils.getTestUser();

    public GoogleSignInSingletonTest(){
        setTestRule(MainActivity.class);
    }

    @Override
    public void initialize() {
        testUser.addToDB(DBUtility.get().getDb_());
        GoogleSignInSingleton.putUniqueID(testUser.getGoogleId_());
    }

    @Test
    public void getUserID(){
        assertEquals(GoogleSignInSingleton.get().getClientUniqueID(), testUser.getGoogleId_());
    }
}
