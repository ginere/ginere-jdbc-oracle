package eu.ginere.jdbc.test;

import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

public class AbstractJDBCTest extends TestCase {

	/**
	 * @param driverClassName
	 * @param url
	 *            jdbc:oracle:thin:@...
	 * @param user
	 *            USER
	 * @param password
	 *            SECRET
	 * @return
	 * @throws SQLException
	 */
	public static DataSource createOracleDataSource(String driverClassName,
													String url,
													String username, 
													String password) throws SQLException {
		return createOracleDataSource("oracle.jdbc.driver.OracleDriver",url,username,password);
	}
	/**
	 * @param driverClassName
	 * @param url
	 *            jdbc:oracle:thin:@1
	 * @param user
	 *            USER
	 * @param password
	 *            SECRET
	 * @return
	 * @throws SQLException
	 */
	public static DataSource createDataSource(String driverClassName,
											  String url,
											  String username, 
											  String password) {
		
		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(driverClassName);
		bds.setUrl(url);
		bds.setUsername(username);
		bds.setPassword(password);

		return bds;
		//
		// oracle.jdbc.pool.OracleDataSource oracleDataSource = new
		// oracle.jdbc.pool.OracleDataSource();
		//
		// oracleDataSource.setURL(url);
		// oracleDataSource.setUser(username);
		// oracleDataSource.setPassword(password);
		// oracleDataSource.setConnectionCachingEnabled(true);
		// Properties prop=oracleDataSource.getConnectionCacheProperties();
		//
		// if (prop!=null){
		// prop.list(System.err);
		// }
		//
		//
		// return oracleDataSource;
	}

}
