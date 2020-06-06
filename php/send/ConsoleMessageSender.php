<?php

require_once 'IMessageSender.php';

class ConsoleMessageSender implements IMessageSender {

	public function send(array $messages) : void {
		printf("| Got %s messages:\n", count($messages));
		foreach ($messages as $message) {
			printf("| >%s\n", $message);
		}
		printf("| DONE\n");
	}

}

?>