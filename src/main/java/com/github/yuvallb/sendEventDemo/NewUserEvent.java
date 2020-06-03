package com.github.yuvallb.sendEventDemo;

import java.util.concurrent.ThreadLocalRandom;

public class NewUserEvent implements IEvent {
	
	private int eventId;
	protected String userEmail;
	public String userName;
	
	public NewUserEvent() {
		eventId = ThreadLocalRandom.current().nextInt();
	}
	public NewUserEvent(String userEmail, String userName) {
		this();
		this.userEmail = userEmail;
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getEventId() {
		return eventId;
	}
}
