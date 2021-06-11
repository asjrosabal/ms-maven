package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Region;
import com.mycompany.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Region}, with proper type conversions.
 */
@Service
public class RegionRowMapper implements BiFunction<Row, String, Region> {

    private final ColumnConverter converter;

    public RegionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Region} stored in the database.
     */
    @Override
    public Region apply(Row row, String prefix) {
        Region entity = new Region();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRegionName(converter.fromRow(row, prefix + "_region_name", String.class));
        return entity;
    }
}
