package eu.ginere.jdbc.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import eu.ginere.base.util.properties.GlobalFileProperties;

public class JDBCTestUtils extends TestCase {

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
	public static DataSource createOracleDataSource(String url,
													String username, 
													String password) throws SQLException {
		return createDataSource("oracle.jdbc.driver.OracleDriver",url,username,password);
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
	/**
	 * @param filePath
	 * @return
	 * @throws NamingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static DataSource createDataSourceFromPropertiesFile(String filePath) throws SQLException, 
		FileNotFoundException,IOException {
		
		filePath=GlobalFileProperties.getPropertiesFilePath(filePath);
		
		Properties prop = new Properties();
		prop.load(new FileInputStream(filePath));

		String url = prop.getProperty("jdbc.url");
		String username = prop.getProperty("jdbc.username");
		String password = prop.getProperty("jdbc.password");
        
		DataSource ret=JDBCTestUtils.createOracleDataSource(url, username, password);
		return ret;
	}

}
