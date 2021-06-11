package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.JobHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the JobHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JobHistoryRepository extends R2dbcRepository<JobHistory, Long>, JobHistoryRepositoryInternal {
    Flux<JobHistory> findAllBy(Pageable pageable);

    @Query("SELECT * FROM job_history entity WHERE entity.job_id = :id")
    Flux<JobHistory> findByJob(Long id);

    @Query("SELECT * FROM job_history entity WHERE entity.job_id IS NULL")
    Flux<JobHistory> findAllWhereJobIsNull();

    @Query("SELECT * FROM job_history entity WHERE entity.department_id = :id")
    Flux<JobHistory> findByDepartment(Long id);

    @Query("SELECT * FROM job_history entity WHERE entity.department_id IS NULL")
    Flux<JobHistory> findAllWhereDepartmentIsNull();

    @Query("SELECT * FROM job_history entity WHERE entity.employee_id = :id")
    Flux<JobHistory> findByEmployee(Long id);

    @Query("SELECT * FROM job_history entity WHERE entity.employee_id IS NULL")
    Flux<JobHistory> findAllWhereEmployeeIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<JobHistory> findAll();

    @Override
    Mono<JobHistory> findById(Long id);

    @Override
    <S extends JobHistory> Mono<S> save(S entity);
}

interface JobHistoryRepositoryInternal {
    <S extends JobHistory> Mono<S> insert(S entity);
    <S extends JobHistory> Mono<S> save(S entity);
    Mono<Integer> update(JobHistory entity);

    Flux<JobHistory> findAll();
    Mono<JobHistory> findById(Long id);
    Flux<JobHistory> findAllBy(Pageable pageable);
    Flux<JobHistory> findAllBy(Pageable pageable, Criteria criteria);
}
