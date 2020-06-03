package com.github.yuvallb.sendEventDemo.serialize;

import com.github.yuvallb.sendEventDemo.IEvent;

public interface IEventSerializer {
	String serialize(IEvent input);
}
