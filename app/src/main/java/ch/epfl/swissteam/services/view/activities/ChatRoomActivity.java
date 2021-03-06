package ch.epfl.swissteam.services.view.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import ch.epfl.swissteam.services.R;
import ch.epfl.swissteam.services.models.ChatMessage;
import ch.epfl.swissteam.services.models.ChatRelation;
import ch.epfl.swissteam.services.models.User;
import ch.epfl.swissteam.services.providers.DBUtility;
import ch.epfl.swissteam.services.providers.GoogleSignInSingleton;
import ch.epfl.swissteam.services.utils.ActivityUtils;
import ch.epfl.swissteam.services.utils.Utils;

import static ch.epfl.swissteam.services.providers.DBUtility.get;


/**
 * This activity is a chat room, it display messages and allow to write and send messages
 *
 * @author Sébastien Gachoud
 */
public class ChatRoomActivity extends NavigationDrawerActivity {

    private FirebaseRecyclerAdapter<ChatMessage, MessageHolder> adapter_;
    private DatabaseReference dataBase_;
    private String currentRelationId_;
    private User mUser_;
    private boolean isDeletedRelation = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_chat_room);
        super.onCreateDrawer(ToogleState.BACK);
        dataBase_ = DBUtility.get().getDb_();

        currentRelationId_ = getIntent().getExtras().getString(ChatRelation.RELATION_ID_TEXT, null);
        checkAndSetIfDeletedByPartner();
        retrieveUserAndSetRelationId();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_message);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy < 0) hideKeyboard();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(ToogleState.NAVIGATION_TAG.toString(), R.id.button_maindrawer_chats);
            startActivity(intent);
        }
    }

    private void retrieveUserAndSetRelationId(){
        DBUtility.get().getUser(GoogleSignInSingleton.get().getClientUniqueID(), mUser -> {
            if(mUser == null){
                toastUser(getResources().getString(R.string.database_could_not_find_you_in_db));
                return;
            }
            mUser_ = mUser;
            String contactId = getIntent().getExtras().getString(NewProfileDetailsActivity.GOOGLE_ID_TAG, null);

            if(currentRelationId_ == null){
                ChatRelation cR = mUser_.relationExists(contactId);
                if(cR == null) {
                    newRelationWith(contactId);
                }
                else {
                    currentRelationId_ = cR.getId_();
                    displayMessages();
                }
            }
            else {
                displayMessages();
            }
        });
    }

    /**
     * display messages retrieved form the database
     */
    private void displayMessages(){
        RecyclerView chatRoom = findViewById(R.id.recycler_view_message);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        chatRoom.setHasFixedSize(true);
        chatRoom.setLayoutManager(llm);

        adapter_ = new ChatRoomAdapter(ChatMessage.class, R.layout.chat_message_layout,
                MessageHolder.class, dataBase_.child(DBUtility.CHATS).child(currentRelationId_));

        adapter_.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRoom.smoothScrollToPosition(adapter_.getItemCount());
            }
        });
        chatRoom.setAdapter(adapter_);
    }

    /**
     * Send message to the database
     * @param view
     */
    public void sendMessage(View view){
        EditText textInput = findViewById(R.id.message_input);
        String message = textInput.getText().toString();
        if(mUser_ == null){
            toastUser(getResources().getString(R.string.database_could_not_find_you_in_db));
            return;
        }
        if(message.isEmpty()){
            return;
        }
        //If nothing works to establish the chat
        if(currentRelationId_ == null) {
            toastUser(getResources().getString(R.string.database_could_not_establish_relation));
            return;
        }
        if(isDeletedRelation){
            toastUser(getResources().getString(R.string.chat_deleted_chat));
        }

        ChatMessage chatMessage = new ChatMessage(message, mUser_.getName_(), mUser_.getGoogleId_(), currentRelationId_);
        chatMessage.addToDB(dataBase_);

        textInput.getText().clear();
    }

    private void newRelationWith(String contactId ){
        DBUtility.get().getUser(contactId, cUser -> {
            if(cUser == null){
                toastUser(getResources().getString(R.string.database_could_not_find_this_user_in_db));
                return;
            }

            ChatRelation newRelation = new ChatRelation(mUser_, cUser);
            newRelation.addToDB(DBUtility.get().getDb_());
            mUser_.addChatRelation(newRelation,  DBUtility.get().getDb_());
            cUser.addChatRelation(newRelation, DBUtility.get().getDb_());
            currentRelationId_ = newRelation.getId_();
            displayMessages();
        });
    }

    private void askToDeleteMessage(ChatMessage message, String key){
        Resources res = getResources();
        Utils.askToDeleteAlertDialog(this, res.getString(R.string.chat_delete_alert_title),
                res.getString(R.string.chat_delete_alert_text), isDeletionSelected -> {
                    if(isDeletionSelected) {
                        message.removeFromDB(get().getDb_(), key);
                    }
                });
    }

    //This method is useful to give this as parameter even inside anonymous declaration
    private void hideKeyboard(){
        ActivityUtils.hideKeyboard(this);
    }

    //This method is useful to give this as parameter even inside anonymous declaration
    private void toastUser(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void checkAndSetIfDeletedByPartner(){
        if(currentRelationId_ != null)
            DBUtility.get().getChatRelation(currentRelationId_, chatRelation ->{
                DBUtility.get().getUser(chatRelation.getOtherId(GoogleSignInSingleton.get().getClientUniqueID()),
                        user-> isDeletedRelation = user == null || user.relationExists(GoogleSignInSingleton.get().getClientUniqueID()) == null);
            });
    }


    /**
     *
     */
    private class ChatRoomAdapter extends FirebaseRecyclerAdapter<ChatMessage, MessageHolder>
    {
        private ChatRoomAdapter(Class<ChatMessage> modelClass, int modelLayout, Class<MessageHolder> viewHolderClass, Query ref) {
            super(modelClass, modelLayout, viewHolderClass, ref);
        }

        @Override
        protected void populateViewHolder(MessageHolder viewHolder, ChatMessage message, int position){
            ViewGroup.LayoutParams rightParams = viewHolder.rightSpace_.getLayoutParams();
            ViewGroup.LayoutParams leftParams = viewHolder.leftSpace_.getLayoutParams();
            if(message.getUserId_().equals(GoogleSignInSingleton.get().getClientUniqueID())){
                rightParams.width = (int)getResources().getDimension(R.dimen.message_shortspace);
                leftParams.width = (int)getResources().getDimension(R.dimen.message_longspace);
            }
            else{
                rightParams.width = (int)getResources().getDimension(R.dimen.message_longspace);
                leftParams.width = (int)getResources().getDimension(R.dimen.message_shortspace);
            }
            viewHolder.rightSpace_.setLayoutParams(rightParams);
            viewHolder.leftSpace_.setLayoutParams(leftParams);

            viewHolder.messageText_.setText(message.getText_());
            viewHolder.timeUserText_.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", message.getTime_()));
            viewHolder.parentLayout_.setOnLongClickListener(new View.OnLongClickListener(){
                private String ref_ = getRef(position).getKey();
                @Override
                public boolean onLongClick(View view) {
                    askToDeleteMessage(message, ref_);
                    return true;
                }
            });
        }
    }
    /**
     * ViewHolder class to handle the RecyclerView
     */
    public static class MessageHolder extends RecyclerView.ViewHolder{
      
        protected TextView messageText_;
        protected TextView timeUserText_;
        protected View parentLayout_;
        protected Space leftSpace_, rightSpace_;

        /**
         * Create a MessageViewHolder
         *
         * @param view the current View
         */
        public MessageHolder(View view) {
            super(view);
            messageText_ = view.findViewById(R.id.message_message);
            timeUserText_ = view.findViewById(R.id.message_time_stamp_user);
            parentLayout_ = view.findViewById(R.id.chat_message_parent_layout);
            leftSpace_ = view.findViewById(R.id.message_leftspace);
            rightSpace_ = view.findViewById(R.id.message_rightspace);
        }
    }
}
