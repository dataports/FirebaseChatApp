package com.example.a774261.firebasechatapp;

import org.junit.Test;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import com.example.a774261.firebasechatapp.ChatMessage;

public class ChatMessageTest {
//define all the test cases


//message should be less than 255 characters, this failing test case is 256
    @Test
    public void testCheckMessageLength_False(){
        String testMessage = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis,.";

        ChatMessage chatMess = new ChatMessage(testMessage, "usertest");


        assertThat(chatMess.checkMessageLength(testMessage), is(false));
    }

   //this is a short message which should return true
    @Test
    public void testCheckMessageLength_True(){
        String testMessage = "Hello";

        ChatMessage chatMess = new ChatMessage(testMessage, "usertest");


        assertThat(chatMess.checkMessageLength(testMessage), is(true));
    }

}
