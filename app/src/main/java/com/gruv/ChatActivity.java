package com.gruv;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gruv.chat.Author;
import com.gruv.chat.Message;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    Message messages = new Message("1234", "HelloWorld", new Author("1234", "Siphe", null), new Date(System.currentTimeMillis()));
    List<IMessage> message = new ArrayList<IMessage>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        MessagesList chatList = (MessagesList)findViewById(R.id.messages_list);
        messages.setText("Hello World");
        MessagesListAdapter adapter = new MessagesListAdapter<>("1234", null);
        adapter.addToEnd(message, false);

        chatList.setAdapter(adapter);
    }
}
