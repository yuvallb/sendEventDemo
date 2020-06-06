<?php

interface IEventSerializer {
	public function serialize(IEvent $input) : string;
}

?>