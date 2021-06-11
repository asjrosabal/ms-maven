package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Region;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Region entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RegionRepository extends R2dbcRepository<Region, Long>, RegionRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<Region> findAll();

    @Override
    Mono<Region> findById(Long id);

    @Override
    <S extends Region> Mono<S> save(S entity);
}

interface RegionRepositoryInternal {
    <S extends Region> Mono<S> insert(S entity);
    <S extends Region> Mono<S> save(S entity);
    Mono<Integer> update(Region entity);

    Flux<Region> findAll();
    Mono<Region> findById(Long id);
    Flux<Region> findAllBy(Pageable pageable);
    Flux<Region> findAllBy(Pageable pageable, Criteria criteria);
}
