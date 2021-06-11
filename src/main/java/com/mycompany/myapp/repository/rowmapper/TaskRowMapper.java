package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Task;
import com.mycompany.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Task}, with proper type conversions.
 */
@Service
public class TaskRowMapper implements BiFunction<Row, String, Task> {

    private final ColumnConverter converter;

    public TaskRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Task} stored in the database.
     */
    @Override
    public Task apply(Row row, String prefix) {
        Task entity = new Task();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
