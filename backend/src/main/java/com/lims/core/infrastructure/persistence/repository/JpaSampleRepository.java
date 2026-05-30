package com.lims.core.infrastructure.persistence.repository;
import io.quarkus.arc.properties.IfBuildProperty;
import com.lims.core.domain.sample.Sample;
import com.lims.core.domain.sample.SampleRepository;
import com.lims.core.infrastructure.persistence.entity.SampleEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

@ApplicationScoped
@IfBuildProperty(name = "lims.db.mock", stringValue = "false", enableIfMissing = true)
public class JpaSampleRepository implements SampleRepository {

    @Inject
    EntityManager em;

    @Override
    @Transactional
    public void save(Sample sample) {
        SampleEntity entity = SampleEntity.fromDomain(sample);
        em.persist(entity);
    }

    @Override
    public List<Sample> findByOrderId(UUID orderId) {
        return em.createQuery("SELECT s FROM SampleEntity s WHERE s.orderId = :oid", SampleEntity.class)
                 .setParameter("oid", orderId)
                 .getResultList().stream()
                 .map(SampleEntity::toDomain)
                 .toList();
    }
    
    
    @Override
    public Optional<Sample> findById(UUID sampleId) {
        return Optional.ofNullable(em.find(SampleEntity.class, sampleId))
                       .map(SampleEntity::toDomain);
    }
     @Override
    public boolean existsByCorrelationId(String correlationId) {
        return em.createQuery("SELECT COUNT(e) FROM SampleEntity e WHERE e.correlationId = :cid", Long.class)
                 .setParameter("cid", correlationId)
                 .getSingleResult() > 0;
    }
}