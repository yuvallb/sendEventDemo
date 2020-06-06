<?php
    require_once 'send/ConsoleMessageSender.php';
    require_once 'serialize/FlatEventSerializer.php';
    require_once 'EventHandler.php';
    require_once 'NewUserEvent.php';

    echo "Demo send event\n";
    echo "Setting gson serializer\n";
    $serializer = new FlatEventSerializer();
    echo "Setting console sender\n";
    $sender = new ConsoleMessageSender();
    echo "Setting NewUserEvent handler\n";
    echo "Dependancy Injection - serializer and sender to the event handler\n";
    
    $handler = new EventHandler($serializer, $sender);

    echo "Creating and handling 20 events\n";
    for ($i=0; $i < 20; $i++) {
        echo "   Randomly sleeping up to 2 seconds\n";
        usleep(random_int(0, 2000000));
        echo "   Generating and sending a NewUserEvent object\n";
        $event = new NewUserEvent(sprintf("user%d@mail.com", $i), sprintf("User%d Name", $i));
        $handler->track($event);
    }
    $handler->flush();

?>