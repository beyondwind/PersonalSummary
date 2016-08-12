package cn.lijiabei.summary.cobar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * insert into tb1(id,gmt) values(1,now()); <br/>
 * insert into tb2(id,val) values(1,"part1"); <br/>
 * insert into tb2(id,val) values(2,"part1"),(513,"part2");
 */
public class InsertTest {

	static String sql1 = "insert into tb1(id,gmt) values(?,now())";
	static String sql2 = "insert into tb2(id,val) values(?,?)";

	public static void main(String[] args) {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:8066/dbtest", "test", "test");
			PreparedStatement preparedStatement1 = conn.prepareStatement(sql1);
			preparedStatement1.setInt(1, 1);
			preparedStatement1.execute();
			preparedStatement1.close();

			PreparedStatement preparedStatement2 = conn.prepareStatement(sql2);
			preparedStatement2.setInt(1, 1);
			preparedStatement2.setString(2, "part1");
			preparedStatement2.execute();

			preparedStatement2.setInt(1, 2);
			preparedStatement2.setString(2, "part1");
			preparedStatement2.execute();

			preparedStatement2.setInt(1, 513);
			preparedStatement2.setString(2, "part2");
			preparedStatement2.execute();
			
			preparedStatement2.close();
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
