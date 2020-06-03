package com.github.yuvallb.sendEventDemo;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.github.yuvallb.sendEventDemo.send.ConsoleMessageSender;
import com.github.yuvallb.sendEventDemo.send.IMessageSender;
import com.github.yuvallb.sendEventDemo.serialize.GsonEventSerializer;
import com.github.yuvallb.sendEventDemo.serialize.IEventSerializer;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Demo send event" );
        System.out.println( "Setting gson serializer" );
        IEventSerializer serializer = new GsonEventSerializer();
        System.out.println( "Setting console sender" );
        IMessageSender sender = new ConsoleMessageSender();
        System.out.println( "Setting NewUserEvent handler" );
        System.out.println( "Dependancy Injection - serializer and sender to the event handler" );
        
        try (EventHandler<NewUserEvent> handler = new EventHandler<NewUserEvent>(serializer, sender)) {
      
	        System.out.println( "Creating and handling 20 events" );
	        for (int i=0; i < 20; i++) {
	            System.out.println( "   Generating and sending a NewUserEvent object");
	        	NewUserEvent event = new NewUserEvent();
	        	event.setUserEmail(String.format("user%d@mail.com", i));
	        	event.setUserName(String.format("User%d Name", i));
	        	handler.track(event);
	            System.out.println( "   Randomly sleeping up to 2 seconds");
	        	TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(2000));
	        }
	        
        }
    }
}
