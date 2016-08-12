package cn.lijiabei.summary.cobar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectTest {

	public static void main(String[] args) {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:8066/dbtest", "test", "test");
			PreparedStatement preparedStatement = conn.prepareStatement("select * from tb2 order by id DESC");

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				System.out.println("id:" + rs.getInt("id") + " val:" + rs.getString("val"));
			}

			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

}
