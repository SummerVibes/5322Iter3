package com.arlington.orm.common;

import java.sql.*;
import java.util.*;

public class DataAccessObject {
	private final Connection conn;

	public DataAccessObject(Connection conn) {
        this.conn = conn;
	}

	public List<Map<String, Object>> executeQuery(String sql) {
        PreparedStatement stmt;
        ResultSet rs;
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            resultList = resultHelper(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;
    }

	public List<Map<String, Object>> executeQuery(String sql, Object[] params) {
		PreparedStatement stmt;
		ResultSet rs;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		try {
			stmt = conn.prepareStatement(sql);
			fillStatement(stmt, params);
			rs = stmt.executeQuery();
			resultList = resultHelper(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultList;
	}

	public int executeUpdate(String sql) {
		PreparedStatement stmt;
		int rs = 0;

		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeUpdate();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		}

		return rs;
	}

	public int executeUpdate(String sql, Object[] params) {
		PreparedStatement stmt;
		int rs = 0;

		try {
			stmt = conn.prepareStatement(sql);
			fillStatement(stmt, params);
			rs = stmt.executeUpdate();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		}

		return rs;
	}

	private List<Map<String, Object>> resultHelper(ResultSet rs) throws SQLException {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		ResultSetMetaData md = rs.getMetaData();
		int cols = md.getColumnCount();

		while (rs.next()) {
			Map<String, Object> map = new HashMap<>();

			for (int i = 0; i < cols; i++) {
				map.put(md.getColumnLabel(i + 1), rs.getObject(i + 1));
			}

			resultList.add(map);
		}

		return resultList;
	}

	private void fillStatement(PreparedStatement stmt, Object[] params) throws SQLException {
		if (params == null) {
			return;
		}
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				stmt.setObject(i + 1, params[i]);
			} else {
				int sqlType = Types.VARCHAR;
				stmt.setNull(i + 1, sqlType);
			}
		}
	}

	public int[] batch(String sqlTemplate,List<Object[]> list) {
		PreparedStatement ps;
		int[] rs = null;

		try{
			ps = conn.prepareStatement(sqlTemplate);

			for (Object[] os : list) {
				for (int j = 0; j < os.length; j++) {
					ps.setObject(j + 1, os[j]);
				}
				ps.addBatch();
			}

			rs = ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return rs;
	}
}
