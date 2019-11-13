package com.gruv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gruv.models.Author;
import com.gruv.models.Message;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imageProfilePic;
    Intent myIntent;
    String title;
    Boolean success;
    Author receive = new Author("1234", null, null, null), send = new Author("1235", "Sipho", null, null);
    MessagesListAdapter<Message> adapter;
    Calendar cal = Calendar.getInstance();
    Date date;
    Message receivedMessage;
    Message sentMessage;
    List<Message> message = new ArrayList<>(), message2 = new ArrayList<>();
    MessageInput inputMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        inputMessage = (MessageInput) findViewById(R.id.editTextInputMessage);
        myIntent = getIntent();
        receive.setName(myIntent.getExtras().getString("sendersName"));

        cal.set(2019, 10, 10, 9, 32);
        date = cal.getTime();
        receivedMessage = new Message("1234", null, receive, date);
        success = setTitleBar();
        MessagesList chatList = (MessagesList) findViewById(R.id.messages_list);

        success = loadMessages(receive.getId(), myIntent.getExtras().getString("messageText"));
        inputMessage.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                //validate and send message
                sendMessage(send.getId(), input.toString());
                return true;
            }

    });
        success = sendMessage(send.getId(), "Aweee");
        chatList.setAdapter(adapter);
    }

    public boolean setTitleBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.main_app_toolbar);
            imageProfilePic = (ImageView) findViewById(R.id.imageProfilePic);
            title = myIntent.getExtras().getString("sendersName");
            toolbar.setTitle(title);
            imageProfilePic.setImageResource(myIntent.getExtras().getInt("pic"));
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    public boolean loadMessages(String senderId, String messageText) {
        try {
            receivedMessage.setText(messageText);
            message.add(receivedMessage);

            adapter = new MessagesListAdapter<>(senderId, null);
            adapter.addToEnd(message, false);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sendMessage(String senderId, String messageText) {
        try {
            sentMessage = new Message(senderId, messageText, send, new Date(System.currentTimeMillis()));
            adapter.addToStart(sentMessage, true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}