package co.com.alpha.bcb.dynamodb.data;

import co.com.alpha.bcb.model.post.generic.DomainEvent;
import co.com.alpha.bcb.serializer.JSONMapper;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Date;

@DynamoDbBean
public class StoredEvent {

    private String aggregateRootId;
    private String eventBody;
    private Date occurredOn;
    private String typeName;

    public StoredEvent() {
    }

    public StoredEvent(String aggregateRootId, String eventBody, Date occurredOn, String typeName) {
        this.aggregateRootId = aggregateRootId;
        this.eventBody = eventBody;
        this.occurredOn = occurredOn;
        this.typeName = typeName;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("aggregateRootId")
    public String getAggregateRootId() {
        return aggregateRootId;
    }

    public void setAggregateRootId(String aggregateRootId) {
        this.aggregateRootId = aggregateRootId;
    }

    @DynamoDbAttribute("eventBody")
    public String getEventBody() {
        return eventBody;
    }

    public void setEventBody(String eventBody) {
        this.eventBody = eventBody;
    }

    @DynamoDbAttribute("occurredOn")
    public Date getOccurredOn() {
        return occurredOn;
    }

    public void setOccurredOn(Date occurredOn) {
        this.occurredOn = occurredOn;
    }

    @DynamoDbAttribute("typeName")
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public static String wrapEvent(DomainEvent domainEvent, JSONMapper eventSerializer){
        return eventSerializer.writeToJson(domainEvent);
    }

    public DomainEvent deserializeEvent(JSONMapper eventSerializer) {
        try{
            return (DomainEvent) eventSerializer
                    .readFromJson(this.getEventBody(), Class.forName(this.getTypeName()));
        }catch (ClassNotFoundException e){
            return null;
        }

    }


    public interface EventSerializer {
        <T extends DomainEvent> T deserialize(String aSerialization, final Class<?> aType);

        String serialize(DomainEvent object);
    }
}