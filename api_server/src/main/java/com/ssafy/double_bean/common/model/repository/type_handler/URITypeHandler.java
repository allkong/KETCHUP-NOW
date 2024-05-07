package com.ssafy.double_bean.common.model.repository.type_handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.VARCHAR)
public class URITypeHandler extends BaseTypeHandler<URI> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, URI parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public URI getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            return new URI(rs.getString(columnName));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public URI getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            return new URI(rs.getString(columnIndex));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public URI getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            return new URI(cs.getString(columnIndex));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, URI parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }
}
