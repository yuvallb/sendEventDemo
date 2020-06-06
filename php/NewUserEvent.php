<?php

require_once 'IEvent.php';

class NewUserEvent implements IEvent {
    private $eventId;
    protected $userEmail;
    public $userName;

    function __construct(string $userEmail, string $userName) {
        $this->eventId = rand(0, PHP_INT_MAX);
        $this->userEmail = $userEmail;
        $this->userName = $userName;
    }
}

?>