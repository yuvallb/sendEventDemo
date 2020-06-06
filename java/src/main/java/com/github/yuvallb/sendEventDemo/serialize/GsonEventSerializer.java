package com.github.yuvallb.sendEventDemo.serialize;

import java.lang.reflect.Modifier;

import com.github.yuvallb.sendEventDemo.IEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonEventSerializer implements IEventSerializer {

	private Gson gson;
	
	public GsonEventSerializer() {
		gson = new GsonBuilder()
			    .excludeFieldsWithModifiers(Modifier.PRIVATE)
			    .create();
	}
	
	public String serialize(IEvent input) {
		return gson.toJson(input);
	}

}
