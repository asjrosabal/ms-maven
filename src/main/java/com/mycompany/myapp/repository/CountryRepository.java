package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Country;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Country entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CountryRepository extends R2dbcRepository<Country, Long>, CountryRepositoryInternal {
    @Query("SELECT * FROM country entity WHERE entity.region_id = :id")
    Flux<Country> findByRegion(Long id);

    @Query("SELECT * FROM country entity WHERE entity.region_id IS NULL")
    Flux<Country> findAllWhereRegionIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Country> findAll();

    @Override
    Mono<Country> findById(Long id);

    @Override
    <S extends Country> Mono<S> save(S entity);
}

interface CountryRepositoryInternal {
    <S extends Country> Mono<S> insert(S entity);
    <S extends Country> Mono<S> save(S entity);
    Mono<Integer> update(Country entity);

    Flux<Country> findAll();
    Mono<Country> findById(Long id);
    Flux<Country> findAllBy(Pageable pageable);
    Flux<Country> findAllBy(Pageable pageable, Criteria criteria);
}
