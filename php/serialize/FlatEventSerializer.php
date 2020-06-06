<?php

require_once 'IEvent.php';
require_once 'IEventSerializer.php';

class FlatEventSerializer implements IEventSerializer {
    public function serialize(IEvent $input) : string {
        $data = [];

        foreach ($this->getProperties($input) as $property) {
            $property->setAccessible(TRUE);
            $data[$property->getName()] = $property->getValue($input);
        }

        return json_encode($data);
    }

    private function getProperties($obj)
    {
        $rc = new ReflectionClass($obj);

        return $rc->getProperties(ReflectionProperty::IS_PUBLIC | ReflectionProperty::IS_PROTECTED);
    }
}

?>