<?php

require_once 'IEvent.php';
require_once 'send/IMessageSender.php';
require_once 'serialize/IEventSerializer.php';

class EventHandler {
	
	// high watermark - send messages if queue is larger than this size
	private $HighWatermark = 40;
	// maximum number of messages in each send bulk
	private $MaxMessageSize = 5;
	// send messages if period elapse
	private $MaxSecondsToWait = 3;
	
	private $queue;
	private $serializer;
	private $sender;
	
	private $lastSent;

	public function __construct(IEventSerializer $serializer, IMessageSender $sender) {
		$this->serializer = $serializer;
		$this->sender = $sender;
		$this->queue = array();
		$this->lastSent = time();
	}
	
	public function track(IEvent $event) : void {
		// add event to queue
		$size = array_unshift($this->queue, $event);
		if ($this->shouldSend($size)) {
			$this->flush();
		}
	}

	private function shouldSend(int $size) : bool {
		return $size >= $this->HighWatermark || 
		  time() > $this->lastSent + $this->MaxSecondsToWait;
	}

	public function flush() : void {
		$this->lastSent = time();
		$messages = array();
		while (true) {
			// get events from queue. will return null if queue empty
			$event = array_pop($this->queue);
			if ($event == NULL) { 
				break;
			}
			// serialize and send messages, bulk up to MaxMessageSize
			array_push($messages, $this->serializer->serialize($event));
			if (count($messages) >= $this->MaxMessageSize) {
				$this->sender->send($messages);
				$messages = array();
			}
		}
		if (count($messages)>0) {
			$this->sender->send($messages);
		}
	}

}
?>