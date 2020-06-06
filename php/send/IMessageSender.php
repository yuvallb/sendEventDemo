<?php

interface IMessageSender {
	public function send(array $messages) : void;
}

?>