package ch.epfl.swissteam.services;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.swissteam.services.models.ChatRelation;
import ch.epfl.swissteam.services.models.User;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ChatRelationTest {
    String name1 = "Martin"; String name2 = "Robin"; String name3 = "Badass";
    String surname1 = "Latex King"; String surname2 = "des Cailloux"; String surname3 = "Romarin";
    String userId1 = "123dfv"; String userId2 = "456dsf"; String userId3 = "789dsf";
    String email1 = "MLK@gmail.com"; String email2 = "RC@gmail.com"; String email3 = "BR@gmail.com";
    User user1 = new User(userId1, name1, email1, "bla", null,
            null,null,  "www.com",0,0,0,null,null, false);
    User user2 = new User(userId2, name2, email2, "bla", null,
            null,null,  "www.com",0,0,0,null,null,false);
    User user3 = new User(userId3, name3, email3, "bla", null,
            null,null,  "www.com",0,0,0,null,null,false);
    User nullUser = new User(null,null,null,null,null,null,
            null,null,0,0.0,0.0,null,null,false);
    String CRId = "abu723ddl92ndk";

    //object Creation
    @Test
    public void objectCreationYieldExpectedValueOnGets() {
        ChatRelation relation = new ChatRelation(user1, user2);

        assertEquals(userId1, relation.getFirstUserId_());
        assertEquals(userId2, relation.getSecondUserId_());
    }

    @Test
    public void objectCreationYieldSortedIds() {
        ChatRelation relation = new ChatRelation(user2, user1);

        assertEquals(userId1, relation.getFirstUserId_());
        assertEquals(userId2, relation.getSecondUserId_());
    }

    @Test(expected = NullPointerException.class)
    public void objectCreationYieldExceptionOnNullValuesForFirstUser() {
        ChatRelation relation = new ChatRelation(null, user2);
    }

    @Test(expected = NullPointerException.class)
    public void objectCreationYieldExceptionOnNullValuesForSecondUser() {
        ChatRelation relation = new ChatRelation(user1, null);
    }

    @Test
    public void UsersIdsAreSorted() {
        ChatRelation relation = new ChatRelation(user1, user2);

        assertEquals(userId1, relation.getFirstUserId_());
        assertEquals(userId2, relation.getSecondUserId_());
    }

    @Test(expected = NullPointerException.class)
    public void cannotInstantiateWithNullFirstUser() {
        new ChatRelation(null, user2);
    }

    @Test(expected = NullPointerException.class)
    public void cannotInstantiateWithNullSecondUser() {
        new ChatRelation(user1, null);
    }

    @Test(expected = NullPointerException.class)
    public void cannotInstantiateWithNullFirstUserId() {
        new ChatRelation(nullUser, user2);
    }

    @Test(expected = NullPointerException.class)
    public void cannotInstantiateWithNullSecondUserId() {
        new ChatRelation(user1, nullUser);
    }

    //setFirstUserId
    @Test
    public void setFirstUserIdYieldRightGetUsersId() {
        ChatRelation relation = new ChatRelation(user2, user3);
        relation.setFirstUserId_(userId1);
        assertEquals(userId1, relation.getFirstUserId_());
        assertEquals(userId3, relation.getSecondUserId_());
    }

    @Test
    public void setFirstUserIdYieldOrderedUsers() {
        ChatRelation relation = new ChatRelation(user1, user2);
        relation.setFirstUserId_(userId3);
        assertEquals(userId2, relation.getFirstUserId_());
        assertEquals(userId3, relation.getSecondUserId_());
    }

    @Test
    public void setFirstUserIdYieldSingleUserRelationWhenTheSecondWasNull() {
        ChatRelation relation = new ChatRelation();
        relation.setFirstUserId_(userId3);
        assertEquals(userId3, relation.getFirstUserId_());
        assertEquals(null, relation.getSecondUserId_());
    }

    @Test(expected = NullPointerException.class)
    public void setFirstUserIdYieldExceptionOnNullValuesForUserId() {
        ChatRelation relation = new ChatRelation(user1, user2);
        relation.setFirstUserId_(null);
    }

    //setSecondUserId
    @Test
    public void setSecondUserIdYieldRightGetUsersId() {
        ChatRelation relation = new ChatRelation(user1, user2);
        relation.setSecondUserId_(userId3);
        assertEquals(userId1, relation.getFirstUserId_());
        assertEquals(userId3, relation.getSecondUserId_());
    }

    @Test
    public void setSecondUserIdYieldOrderedUsers() {
        ChatRelation relation = new ChatRelation(user2, user3);
        relation.setSecondUserId_(userId1);
        assertEquals(userId1, relation.getFirstUserId_());
        assertEquals(userId2, relation.getSecondUserId_());
    }

    @Test
    public void setSecondUserIdYieldSingleUserRelationWhenTheSecondWasNull() {
        ChatRelation relation = new ChatRelation();
        relation.setSecondUserId_(userId3);
        assertEquals(null, relation.getFirstUserId_());
        assertEquals(userId3, relation.getSecondUserId_());
    }

    @Test(expected = NullPointerException.class)
    public void setSecondUserIdYieldExceptionOnNullValuesForUserId() {
        ChatRelation relation = new ChatRelation(user1, user2);
        relation.setSecondUserId_(null);
    }

    //setId
    @Test
    public void setIdGivesBackSameWhenGet() {

        ChatRelation relation = new ChatRelation(user1, user2);

        relation.setId_(CRId);
        assertEquals(CRId, relation.getId_());
    }

    //getOtherId
    @Test
    public void getOtherIdWorks(){
        ChatRelation relation = new ChatRelation(user1, user2);

        assertEquals(userId1, relation.getOtherId(userId2));
        assertEquals(userId2, relation.getOtherId(userId1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOtherIdThrowsWhenWrongUser(){
        ChatRelation relation = new ChatRelation(user1, user2);
        relation.getOtherId(userId3);
    }
}
