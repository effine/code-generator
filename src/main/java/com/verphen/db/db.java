package main.java.com.verphen.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import main.java.com.verphen.model.Column;
import main.java.com.verphen.model.Table;
import main.java.com.verphen.utils.TypeConvertUtil;

public class db {

	public static Connection con = null;
	public static Statement stat = null;

	public static void init() {
		getCon("mysql", "192.168.0.120", 3306, "root", "Yunluqwe");
		// getBruceConn();
	}

	public static void initBruce() {
		// getCon("mysql", "192.168.0.120", 3306, "root", "Yunluqwe");
		getBruceConn();
	}

	public static void init(String type, String dataSource, Integer port,
			String user, String password) {
		getCon(type, dataSource, port, user, password);
	}

	public static List<String> getDatabase() {
		List<String> list = new ArrayList<String>();
		try {
			String sql = "SHOW databases";
			ResultSet rs = stat.executeQuery(sql);
			String name = null;
			int order = 1;
			while (rs.next()) {
				name = rs.getNString(order);
				name = new String(name.getBytes("ISO-8859-1"), "GB2312");
				list.add(name);
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	private static void getCon(String type, String dataSource, Integer port,
			String user, String password) {
		// 驱动程序名
		String driver = "";
		String urlstart = "";
		if (type.equals("mysql")) {
			driver = "com.mysql.jdbc.Driver";
			urlstart = "jdbc:mysql://";
			// urlstart =
			// "jdbc:mysql://192.168.0.120:3306/yunlu?characterEncoding=UTF-8";
			if (port == null)
				port = 3306;
		}
		String url = urlstart + dataSource + ":" + port;
		System.out.println(url + "-------url");
		try {
			// 加载驱动程序
			Class.forName(driver);
			// 连续数据库
			con = DriverManager.getConnection(url, user, password);
			if (!con.isClosed())
				System.out.println("Succeeded connecting to the Database!");
			// statement用来执行SQL语句
			stat = con.createStatement();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Sorry,can`t find the Driver!");
		}

	}

	private static void getBruceConn() {
		// 驱动程序名
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://192.168.0.120:3306/yunlu";
		System.out.println(url + "-------url");
		try {
			// 加载驱动程序
			Class.forName(driver);
			// 连续数据库
			con = DriverManager.getConnection(url, "root", "Yunluqwe");
			if (!con.isClosed())
				System.out.println("Succeeded connecting to the Database!");
			// statement用来执行SQL语句
			stat = con.createStatement();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Sorry,can`t find the Driver!");
		}

	}

	// public static void closeConn(){
	// try {
	// con.close();
	// } catch(Exception e) {
	// System.out.println("Sorry,can`t find the Driver!");
	// }
	// }

	public static List<String> getTable(String dataBaseName) {
		List<String> list = new ArrayList<String>();
		try {
			String useSQL = "use " + dataBaseName;
			stat.executeQuery(useSQL);
			String sql = " SHOW TABLES";
			ResultSet rs = stat.executeQuery(sql);
			String name = null;
			int order = 1;
			while (rs.next()) {
				name = rs.getNString(order);
				name = new String(name.getBytes("ISO-8859-1"), "GB2312");
				list.add(name);
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static Table getColumnByOneTable(String tableName) {
		Table tb = null;
		try {
			String sql = "show columns from " + tableName;
			ResultSet rs = stat.executeQuery(sql);
			int length = 0;
			tb = getTableByTableName(tableName);

			List<Column> list = new ArrayList<Column>();
			while (rs.next()) {
				String column_field = null;
				String column_type = null;
				column_field = rs.getString("field");
				column_type = rs.getString("type");
				String pk = rs.getString("key");
				length = getLength(length, column_type);

				column_field = new String(column_field.getBytes("ISO-8859-1"),
						"GB2312");
				column_type = new String(column_type.getBytes("ISO-8859-1"),
						"GB2312");
				Column p = new Column();
				p.setType(TypeConvertUtil.getType(column_type));
				p.setName(column_field);
				p.setColumn(getCloumnName(column_field));
				p.setLength(length);
				list.add(p);
			}
			tb.setPropertyList(list);
			// rs.close();
			// closeConn();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tb;
	}

	public static List<Table> getColumn(List<String> tableNames) {

		List<Table> listTb = new ArrayList<Table>();
		try {
			for (int i = 0; i < tableNames.size(); i++) {
				String tableName = tableNames.get(i);
				// if("yl_course".equals(tableName)) {
				// System.out.println(tableName);
				// continue;
				// }
				if (tableName != null && !tableName.equals("")) {
					String sql = "show columns from " + tableName;
					ResultSet rs = stat.executeQuery(sql);
					int length = 0;
					Table tb = getTableByTableName(tableName);

					List<Column> list = new ArrayList<Column>();
					while (rs.next()) {
						String column_field = null;
						String column_type = null;
						column_field = rs.getString("field");
						column_type = rs.getString("type");
						String pk = rs.getString("key");
						length = getLength(length, column_type);
						column_field = new String(
								column_field.getBytes("ISO-8859-1"), "GB2312");
						column_type = new String(
								column_type.getBytes("ISO-8859-1"), "GB2312");
						Column p = new Column();
						p.setType(TypeConvertUtil.getType(column_type));
						p.setName(column_field);
						p.setColumn(getCloumnName(column_field));
						p.setLength(length);
						list.add(p);
					}
					tb.setPropertyList(list);
					listTb.add(tb);
					// rs.close();
				}
			}
			// closeConn();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listTb;
	}

	private static int getLength(int length, String column_type) {
		if (column_type.contains("(")) {
			length = Integer.parseInt(column_type.substring(
					column_type.indexOf("(") + 1, column_type.indexOf(")")));
		}
		return length;
	}

	public static Table getTableByTableName(String tableName) {
		Table tb = new Table();
		String[] spTbName = tableName.split("_");
		tb.setTableName(tableName);
		if (spTbName.length > 1) {
			tb.setPackageName(spTbName[0]);
			tb.setModelName(getModelName(spTbName));
		} else {
			tb.setPackageName("");
			tb.setModelName(spTbName[0]);
		}
		return tb;
	}

	public static String getModelName(String[] name) {
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < name.length; i++) {
			String s = name[i].toLowerCase();
			String index = s.substring(0, 1).toUpperCase();
			String el = s.substring(1);
			sb.append(index + el);
		}
		return sb.toString();
	}

	// public static String getCloumnName(String name) {
	// String[] m = name.split("_");
	// StringBuffer sb = new StringBuffer();
	// sb.append(m[0].toLowerCase());
	// for (int i = 1; i < m.length; i++) {
	// String index = m[i].substring(0, 1).toUpperCase();
	// String el = m[i].substring(1).toLowerCase();
	// sb.append(index + el);
	// }
	// return sb.toString();
	// }

	public static String getCloumnName(String name) {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		return sb.toString();
	}
}
