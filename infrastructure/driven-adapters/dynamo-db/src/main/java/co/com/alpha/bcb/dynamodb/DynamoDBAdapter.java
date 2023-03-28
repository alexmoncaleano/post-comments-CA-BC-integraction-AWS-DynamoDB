package co.com.alpha.bcb.dynamodb;
import co.com.alpha.bcb.dynamodb.data.StoredEvent;
import co.com.alpha.bcb.dynamodb.mapper.StoredEventMapper;
import co.com.alpha.bcb.model.post.generic.DomainEvent;
import co.com.alpha.bcb.serializer.JSONMapper;
import co.com.alpha.bcb.usecase.generic.gateways.DomainEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.Date;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DynamoDBAdapter implements DomainEventRepository {

    @Value("${aws.dynamodb.table}") String table;

    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    private final JSONMapper eventSerializer;
    @Override
    public Flux<DomainEvent> findById(String aggregateId) {
        return null;
    }

    @Override
    public Mono<DomainEvent> saveEvent(DomainEvent event) {
        StoredEvent storedEvent = new StoredEvent();
        buildStoredEvent(event, storedEvent);

        return Mono.fromFuture(dynamoDbAsyncClient.putItem(getPutItemRequest(storedEvent)))
                .map(PutItemResponse::attributes)
                .map(map -> storedEvent.deserializeEvent(eventSerializer));
    }

    private PutItemRequest getPutItemRequest(StoredEvent storedEvent) {
        return PutItemRequest.builder()
                .tableName(table)
                .item(StoredEventMapper.convertToStoredEventEntity(storedEvent))
                .build();
    }

    private void buildStoredEvent(DomainEvent event, StoredEvent storedEvent) {
        storedEvent.setAggregateRootId(event.aggregateRootId());
        storedEvent.setTypeName(event.getClass().getTypeName());
        storedEvent.setOccurredOn(new Date());
        storedEvent.setEventBody(StoredEvent.wrapEvent(event, eventSerializer));
    }
}