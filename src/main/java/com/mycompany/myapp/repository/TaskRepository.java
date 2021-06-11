package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Task entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskRepository extends R2dbcRepository<Task, Long>, TaskRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<Task> findAll();

    @Override
    Mono<Task> findById(Long id);

    @Override
    <S extends Task> Mono<S> save(S entity);
}

interface TaskRepositoryInternal {
    <S extends Task> Mono<S> insert(S entity);
    <S extends Task> Mono<S> save(S entity);
    Mono<Integer> update(Task entity);

    Flux<Task> findAll();
    Mono<Task> findById(Long id);
    Flux<Task> findAllBy(Pageable pageable);
    Flux<Task> findAllBy(Pageable pageable, Criteria criteria);
}
