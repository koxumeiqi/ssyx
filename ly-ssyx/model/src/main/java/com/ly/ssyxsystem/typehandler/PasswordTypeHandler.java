package com.ly.ssyxsystem.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

/**
 * 对密码进行再次加密处理
 */
public class PasswordTypeHandler extends BaseTypeHandler<String> {


    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i,
                                    String s, JdbcType jdbcType) throws SQLException {
        String pwd = Base64.getEncoder().encodeToString(s.getBytes());
        preparedStatement.setString(i, pwd);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String pwd = resultSet.getString(s);
        String rawPwd = new String(Base64.getDecoder().decode(pwd.getBytes()));
        return rawPwd;
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String pwd = resultSet.getString(i);
        String rawPwd = new String(Base64.getDecoder().decode(pwd.getBytes()));
        return rawPwd;
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String pwd = callableStatement.getString(i);
        String rawPwd = new String(Base64.getDecoder().decode(pwd.getBytes()));
        return rawPwd;
    }
}
