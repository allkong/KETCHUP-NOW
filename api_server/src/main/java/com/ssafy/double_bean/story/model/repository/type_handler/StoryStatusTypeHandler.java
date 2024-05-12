package com.ssafy.double_bean.story.model.repository.type_handler;

import com.ssafy.double_bean.story.model.entity.StoryEntity;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.VARCHAR)
public class StoryStatusTypeHandler extends BaseTypeHandler<StoryEntity.StoryStatus> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, StoryEntity.StoryStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public StoryEntity.StoryStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return StoryEntity.StoryStatus.valueOf(rs.getString(columnName));
    }

    @Override
    public StoryEntity.StoryStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return StoryEntity.StoryStatus.valueOf(rs.getString(columnIndex));
    }

    @Override
    public StoryEntity.StoryStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return StoryEntity.StoryStatus.valueOf(cs.getString(columnIndex));
    }
}
