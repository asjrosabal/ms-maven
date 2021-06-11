package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends R2dbcRepository<Employee, Long>, EmployeeRepositoryInternal {
    Flux<Employee> findAllBy(Pageable pageable);

    @Query("SELECT * FROM employee entity WHERE entity.manager_id = :id")
    Flux<Employee> findByManager(Long id);

    @Query("SELECT * FROM employee entity WHERE entity.manager_id IS NULL")
    Flux<Employee> findAllWhereManagerIsNull();

    @Query("SELECT * FROM employee entity WHERE entity.department_id = :id")
    Flux<Employee> findByDepartment(Long id);

    @Query("SELECT * FROM employee entity WHERE entity.department_id IS NULL")
    Flux<Employee> findAllWhereDepartmentIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Employee> findAll();

    @Override
    Mono<Employee> findById(Long id);

    @Override
    <S extends Employee> Mono<S> save(S entity);
}

interface EmployeeRepositoryInternal {
    <S extends Employee> Mono<S> insert(S entity);
    <S extends Employee> Mono<S> save(S entity);
    Mono<Integer> update(Employee entity);

    Flux<Employee> findAll();
    Mono<Employee> findById(Long id);
    Flux<Employee> findAllBy(Pageable pageable);
    Flux<Employee> findAllBy(Pageable pageable, Criteria criteria);
}
