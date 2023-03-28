package co.com.alpha.bcb.dynamodb.mapper;

import co.com.alpha.bcb.dynamodb.data.StoredEvent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

public interface StoredEventMapper {

    static StoredEvent convertToStoredEvent(Map<String, AttributeValue> attributeMap){
        StoredEvent storedEvent = new StoredEvent();
        storedEvent.setAggregateRootId(attributeMap.get("aggregateRootId").s());
        storedEvent.setEventBody(attributeMap.get("eventBody").s());
        storedEvent.setOccurredOn(Date.from(Instant.parse(attributeMap.get("occurredOn").s())));
        storedEvent.setTypeName(attributeMap.get("typeName").s());
        return storedEvent;
    }

    static Map<String, AttributeValue> convertToStoredEventEntity(StoredEvent storedEvent){

        return Map.of(
                "aggregateRootId", AttributeValue.builder().s(storedEvent.getAggregateRootId()).build(),
                "eventBody", AttributeValue.builder().s(storedEvent.getEventBody()).build(),
                "occurredOn", AttributeValue.builder().s(storedEvent.getOccurredOn().toString()).build(),
                "typeName", AttributeValue.builder().s(storedEvent.getTypeName()).build());

    }
}