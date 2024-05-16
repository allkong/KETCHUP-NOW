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
        if (parameter != null) {
            ps.setString(i, parameter.toString());
        } else {
            ps.setString(i, null);
        }
    }

    @Override
    public URI getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            if (rs.getString(columnName) != null) {
                return new URI(rs.getString(columnName));
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public URI getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            if (rs.getString(columnIndex) != null) {
                return new URI(rs.getString(columnIndex));
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public URI getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            if (cs.getString(columnIndex) != null) {
                return new URI(cs.getString(columnIndex));
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, URI parameter, JdbcType jdbcType) throws SQLException {
        if (parameter != null) {
            ps.setString(i, parameter.toString());
        } else {
            ps.setString(i, null);
        }
    }
}
