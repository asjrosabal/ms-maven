package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Department;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Department entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DepartmentRepository extends R2dbcRepository<Department, Long>, DepartmentRepositoryInternal {
    @Query("SELECT * FROM department entity WHERE entity.location_id = :id")
    Flux<Department> findByLocation(Long id);

    @Query("SELECT * FROM department entity WHERE entity.location_id IS NULL")
    Flux<Department> findAllWhereLocationIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Department> findAll();

    @Override
    Mono<Department> findById(Long id);

    @Override
    <S extends Department> Mono<S> save(S entity);
}

interface DepartmentRepositoryInternal {
    <S extends Department> Mono<S> insert(S entity);
    <S extends Department> Mono<S> save(S entity);
    Mono<Integer> update(Department entity);

    Flux<Department> findAll();
    Mono<Department> findById(Long id);
    Flux<Department> findAllBy(Pageable pageable);
    Flux<Department> findAllBy(Pageable pageable, Criteria criteria);
}
