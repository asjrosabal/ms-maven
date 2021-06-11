package com.mycompany.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mycompany.myapp.domain.JobHistory;
import com.mycompany.myapp.domain.enumeration.Language;
import com.mycompany.myapp.repository.rowmapper.DepartmentRowMapper;
import com.mycompany.myapp.repository.rowmapper.EmployeeRowMapper;
import com.mycompany.myapp.repository.rowmapper.JobHistoryRowMapper;
import com.mycompany.myapp.repository.rowmapper.JobRowMapper;
import com.mycompany.myapp.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the JobHistory entity.
 */
@SuppressWarnings("unused")
class JobHistoryRepositoryInternalImpl implements JobHistoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final JobRowMapper jobMapper;
    private final DepartmentRowMapper departmentMapper;
    private final EmployeeRowMapper employeeMapper;
    private final JobHistoryRowMapper jobhistoryMapper;

    private static final Table entityTable = Table.aliased("job_history", EntityManager.ENTITY_ALIAS);
    private static final Table jobTable = Table.aliased("job", "job");
    private static final Table departmentTable = Table.aliased("department", "department");
    private static final Table employeeTable = Table.aliased("employee", "employee");

    public JobHistoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        JobRowMapper jobMapper,
        DepartmentRowMapper departmentMapper,
        EmployeeRowMapper employeeMapper,
        JobHistoryRowMapper jobhistoryMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.jobMapper = jobMapper;
        this.departmentMapper = departmentMapper;
        this.employeeMapper = employeeMapper;
        this.jobhistoryMapper = jobhistoryMapper;
    }

    @Override
    public Flux<JobHistory> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<JobHistory> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<JobHistory> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = JobHistorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(JobSqlHelper.getColumns(jobTable, "job"));
        columns.addAll(DepartmentSqlHelper.getColumns(departmentTable, "department"));
        columns.addAll(EmployeeSqlHelper.getColumns(employeeTable, "employee"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(jobTable)
            .on(Column.create("job_id", entityTable))
            .equals(Column.create("id", jobTable))
            .leftOuterJoin(departmentTable)
            .on(Column.create("department_id", entityTable))
            .equals(Column.create("id", departmentTable))
            .leftOuterJoin(employeeTable)
            .on(Column.create("employee_id", entityTable))
            .equals(Column.create("id", employeeTable));

        String select = entityManager.createSelect(selectFrom, JobHistory.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<JobHistory> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<JobHistory> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private JobHistory process(Row row, RowMetadata metadata) {
        JobHistory entity = jobhistoryMapper.apply(row, "e");
        entity.setJob(jobMapper.apply(row, "job"));
        entity.setDepartment(departmentMapper.apply(row, "department"));
        entity.setEmployee(employeeMapper.apply(row, "employee"));
        return entity;
    }

    @Override
    public <S extends JobHistory> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends JobHistory> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update JobHistory with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(JobHistory entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class JobHistorySqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("start_date", table, columnPrefix + "_start_date"));
        columns.add(Column.aliased("end_date", table, columnPrefix + "_end_date"));
        columns.add(Column.aliased("language", table, columnPrefix + "_language"));

        columns.add(Column.aliased("job_id", table, columnPrefix + "_job_id"));
        columns.add(Column.aliased("department_id", table, columnPrefix + "_department_id"));
        columns.add(Column.aliased("employee_id", table, columnPrefix + "_employee_id"));
        return columns;
    }
}
