package org.fiware.tmforum.resourceinventory.rest;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiware.tmforum.common.domain.EntityWithId;
import org.fiware.tmforum.common.mapping.IdHelper;
import org.fiware.tmforum.common.validation.ReferenceValidationService;
import org.fiware.tmforum.common.validation.ReferencedEntity;
import org.fiware.tmforum.resourceinventory.TMForumMapper;
import org.fiware.tmforum.resourceinventory.exception.ResourceInventoryException;
import org.fiware.tmforum.resourceinventory.exception.ResourceInventoryExceptionReason;
import org.fiware.tmforum.resourceinventory.repository.ResourceInventoryRepository;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.fiware.tmforum.common.CommonConstants.DEFAULT_LIMIT;
import static org.fiware.tmforum.common.CommonConstants.DEFAULT_OFFSET;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractApiController {


    protected final TMForumMapper tmForumMapper;
    protected final ReferenceValidationService validationService;
    protected final ResourceInventoryRepository resourceInventoryRepository;

    protected <T> Mono<T> getCheckingMono(T entityToCheck, List<List<? extends ReferencedEntity>> referencedEntities) {
        Mono<T> checkingMono = Mono.just(entityToCheck);
        for (List<? extends ReferencedEntity> referencedEntitiesList : referencedEntities) {
            if (referencedEntitiesList != null && !referencedEntitiesList.isEmpty()) {
                checkingMono = Mono.zip(checkingMono, validationService.getCheckingMono(referencedEntitiesList, entityToCheck), (p1, p2) -> p1);
            }
        }
        return checkingMono;
    }

    protected <T> Mono<T> create(Mono<T> checkingMono, Class<T> entityClass) {
        return checkingMono
                .flatMap(checkedResult -> resourceInventoryRepository.createDomainEntity(checkedResult).then(Mono.just(checkedResult)))
                .onErrorMap(t -> {
                    if (t instanceof HttpClientResponseException e) {
                        return switch (e.getStatus()) {
                            case CONFLICT -> new ResourceInventoryException(String.format("Conflict on creating the entity: %s", e.getMessage()), ResourceInventoryExceptionReason.CONFLICT);
                            case BAD_REQUEST -> new ResourceInventoryException(String.format("Did not receive a valid entity: %s.", e.getMessage()), ResourceInventoryExceptionReason.INVALID_DATA);
                            default -> new ResourceInventoryException(String.format("Unspecified downstream error: %s", e.getMessage()), ResourceInventoryExceptionReason.UNKNOWN);
                        };
                    } else {
                        return t;
                    }
                })
                .cast(entityClass);
    }

    protected Mono<HttpResponse<Object>> delete(String id) {
        // non-ngsi-ld ids cannot exist.
        if (!IdHelper.isNgsiLdId(id)) {
            throw new ResourceInventoryException("Did not receive a valid id, such entity cannot exist.", ResourceInventoryExceptionReason.NOT_FOUND);
        }
        return resourceInventoryRepository.deleteDomainEntity(URI.create(id))
                .then(Mono.just(HttpResponse.noContent()));
    }

    protected <R> Mono<Stream<R>> list(Integer offset, Integer limit, String type, Class<R> entityClass) {
        offset = Optional.ofNullable(offset).orElse(DEFAULT_OFFSET);
        limit = Optional.ofNullable(limit).orElse(DEFAULT_LIMIT);

        if (offset < 0 || limit < 1) {
            throw new ResourceInventoryException(String.format("Invalid offset %s or limit %s.", offset, limit), ResourceInventoryExceptionReason.INVALID_DATA);
        }

        return resourceInventoryRepository
                .findEntities(offset, limit, type, entityClass)
                .map(List::stream);
    }

    protected <R> Mono<R> retrieve(String id, Class<R> entityClass) {
        // non-ngsi-ld ids cannot exist.
        if (!IdHelper.isNgsiLdId(id)) {
            throw new ResourceInventoryException("Did not receive a valid id, such inventory cannot exist.", ResourceInventoryExceptionReason.NOT_FOUND);
        }
        return resourceInventoryRepository
                .get(URI.create(id), entityClass);
    }

    protected <T> Mono<T> patch(String id, T updatedObject, Mono<T> checkingMono, Class<T> entityClass) {
        URI idUri = URI.create(id);

        return resourceInventoryRepository
                .get(idUri, entityClass)
                .switchIfEmpty(Mono.error(new ResourceInventoryException("No such entity exists.", ResourceInventoryExceptionReason.NOT_FOUND)))
                .flatMap(entity -> checkingMono)
                .flatMap(entity -> resourceInventoryRepository.updateDomainEntity(id, updatedObject)
                        .then(resourceInventoryRepository.get(idUri, entityClass)));
    }


    protected <T, R extends EntityWithId> Mono<T> relatedEntityHandlingMono(T entity, Mono<T> entityMono, List<R> relatedList, Consumer<List<R>> entityUpdater, Class<R> relatedEntityClass) {
        List<R> relatedEntities = Optional.ofNullable(relatedList).orElseGet(List::of);
        if (!relatedEntities.isEmpty()) {
            Mono<List<R>> relatedEntitiesMono = Mono.zip(
                    relatedEntities
                            .stream()
                            .map(re ->
                                    resourceInventoryRepository
                                            .updateDomainEntity(re.getId().toString(), re)
                                            .onErrorResume(t -> resourceInventoryRepository.createDomainEntity(re))
                                            .then(Mono.just(re))
                            )
                            .toList(),
                    t -> Arrays.stream(t).map(relatedEntityClass::cast).toList());

            Mono<T> updatingMono = relatedEntitiesMono
                    .map(updatedRelatedEntity -> {
                        entityUpdater.accept(updatedRelatedEntity);
                        return entity;
                    });
            entityMono = Mono.zip(entityMono, updatingMono, (e1, e2) -> e1);
        }
        return entityMono;
    }


}
