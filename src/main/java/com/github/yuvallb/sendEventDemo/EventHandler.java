package com.github.yuvallb.sendEventDemo;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.github.yuvallb.sendEventDemo.send.IMessageSender;
import com.github.yuvallb.sendEventDemo.serialize.IEventSerializer;

public class EventHandler<T extends IEvent> implements AutoCloseable {
	
	// high watermark - send messages if queue is larger than this size
	private static final int HighWatermark = 4;
	// maximum number of messages in each send bulk
	private static final int MaxMessageSize = 5;
	// async send messages from a background thread every preset period
	private static final int BackgroundFlushSeconds = 3;
	
	private Deque<T> queue;
	private IEventSerializer serializer;
	private IMessageSender sender;
	
	private Timer flushTimer;
	
	public EventHandler(IEventSerializer serializer, IMessageSender sender) {
		this.serializer = serializer;
		this.sender = sender;
		queue = new LinkedList<T>();
		flushTimer = new Timer("FlushTimer");
		flushTimer.schedule(flushTask, BackgroundFlushSeconds * 1000, BackgroundFlushSeconds * 1000);
	}
	
	public void track(T event) {
		// add event to queue
		queue.add(event);
		// in case we reach high watermark before background flush - do the flush synchronously
		if (queue.size() >= HighWatermark) {
			flush();
		}
	}
	
	public void flush() {
		List<String> messages = new ArrayList<String>();
		while (true) {
			// get events from queue. will return null if queue empty
			T event = queue.poll();
			if (event == null) { 
				break;
			}
			// serialize and send messages, bulk up to MaxMessageSize
			messages.add(serializer.serialize(event));
			if (messages.size() >= MaxMessageSize) {
				sender.send(messages);
				messages.clear();
			}
		}
		if (messages.size()>0) {
			sender.send(messages);
		}
 	}
	
	private TimerTask flushTask = new TimerTask() {
        public void run() {
            flush();
        }
    };

	public void close() throws Exception {
		flushTimer.cancel();
		flush();
	}
}
