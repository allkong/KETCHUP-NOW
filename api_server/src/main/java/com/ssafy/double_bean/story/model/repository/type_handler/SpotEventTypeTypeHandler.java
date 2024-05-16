package com.ssafy.double_bean.story.model.repository.type_handler;

import com.ssafy.double_bean.story.model.entity.SpotEntity;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.VARCHAR)
public class SpotEventTypeTypeHandler extends BaseTypeHandler<SpotEntity.EventType> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, SpotEntity.EventType parameter, JdbcType jdbcType) throws SQLException {
        if (parameter != null) {
            ps.setString(i, parameter.name());
        }
    }

    @Override
    public SpotEntity.EventType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        if (rs.getString(columnName) != null) {
            return SpotEntity.EventType.valueOf(rs.getString(columnName));
        } else {
            return null;
        }
    }

    @Override
    public SpotEntity.EventType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        if (rs.getString(columnIndex) != null) {
            return SpotEntity.EventType.valueOf(rs.getString(columnIndex));
        } else {
            return null;
        }
    }

    @Override
    public SpotEntity.EventType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (cs.getString(columnIndex) != null) {
            return SpotEntity.EventType.valueOf(cs.getString(columnIndex));
        } else {
            return null;
        }
    }
}
