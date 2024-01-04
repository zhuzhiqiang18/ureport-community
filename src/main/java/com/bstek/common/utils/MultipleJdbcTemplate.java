package com.bstek.common.utils;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.jdbc.support.JdbcUtils;

import com.bstek.datasource.bean.DataSourceInfo;

public class MultipleJdbcTemplate {
	
	public static Connection buildConnection(DataSourceInfo info) {
		try {
			String username = info.getUserName();
			String password = info.getPassword();
			String driver = info.getDriverClassName();
			String url = info.getUrl();
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, username, password);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String testConnection(DataSourceInfo info) {
		Connection conn = null;
		try {
			String username = info.getUserName();
			String password = info.getPassword();
			String driver = info.getDriverClassName();
			String url = info.getUrl();
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			return e.getMessage();
		} finally{
			JdbcUtils.closeConnection(conn);
		}
		return "success";
	}
}
