package com.github.yuvallb.sendEventDemo.send;

import java.util.List;

public class ConsoleMessageSender implements IMessageSender {

	public void send(List<String> messages) {
		if (messages == null) {
			System.out.println("| Got null message");
			return;
		}
		System.out.println(String.format("| Got %d messages:", messages.size()));
		for (String message: messages) {
			System.out.println("|  >  " + message);
		}
		System.out.println("| DONE");
	}

}
