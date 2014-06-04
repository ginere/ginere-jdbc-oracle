package eu.ginere.jdbc.oracle.configuration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import eu.ginere.jdbc.oracle.AbstractDAO;
import eu.ginere.jdbc.oracle.DaoManagerException;


public class ConfigurationDAO extends AbstractDAO implements ConfigurationDAOInterface{
	
	static final Logger log = Logger.getLogger(ConfigurationDAO.class);

	private static final String TABLE_NAME = "CONFIGURACION";

	
	public static final ConfigurationDAO DAO=new ConfigurationDAO();

	
	public static final String GET_PROPERTY="select VALUE from "+TABLE_NAME+" where NAME=?1 and rownum <=1";
	
	public String getProperty(String propertyName) throws DaoManagerException {
		try {
			Connection connection = getConnection();
			String query=GET_PROPERTY;
			
			try {
				PreparedStatement pstm = getPrepareStatement(connection,query);
				
				setString(pstm, 1, propertyName, query);
				
				return getString(pstm, query);
				
			} finally {
				closeConnection(connection);
			}
		}catch (DaoManagerException e) {
			throw new DaoManagerException("propertyName:"+propertyName+"'",e);
		}
	}
	
	public boolean exists(String propertyName) throws DaoManagerException {
		try {
			Connection connection = getConnection();
			String query=GET_PROPERTY;
			
			try {
				PreparedStatement pstm = getPrepareStatement(connection,query);
				
				setString(pstm, 1, propertyName, query);
				
				ResultSet rset = executeQuery(pstm,query);
				
				return rset.next();
			} catch (SQLException e) {
				String error = " propertyName:'"+propertyName+"'";
				
				throw new DaoManagerException(error, e);			
			} catch (DaoManagerException e) {
				String error = " propertyName:'"+propertyName+"'";
				
				throw new DaoManagerException(error, e);
			} finally {
				closeConnection(connection);
			}
		}catch (DaoManagerException e) {
			throw new DaoManagerException("propertyName:"+propertyName+"'",e);
		}
	}

	public static final String INSERT_PROPERTY="insert into "+TABLE_NAME+" (NAME,VALUE,DESCRIPTION) VALUES (?1,?2,?3)";

	public void insertProperty(String propertyName,
							   String defaultValue,
							   String description) throws DaoManagerException {
		try {
			Connection connection = getConnection();
			String query=INSERT_PROPERTY;
			
			try {
				PreparedStatement pstm = getPrepareStatement(connection,query);
				
				setString(pstm, 1, propertyName, query);
				setString(pstm, 2, defaultValue, query);
				setString(pstm, 3, description, query);
				
				long rowNum=executeUpdate(pstm, query);
				
				if (log.isDebugEnabled()){
					log.debug("Lineas actualizadas:"+rowNum);			
				}	
			} finally {
				closeConnection(connection);
			}
		}catch (DaoManagerException e) {
			throw new DaoManagerException("propertyName:"+propertyName+
										  "', defaultValue:"+defaultValue+
										  "' description:"+description+
										  "'",e);
		}
	}

	public static final String DELETE_PROPERTY="delete from "+TABLE_NAME+" where NAME=?1";

	public void delete(String propertyName) throws DaoManagerException {
		try {
			Connection connection = getConnection();
			String query=DELETE_PROPERTY;
			
			try {
				PreparedStatement pstm = getPrepareStatement(connection,query);
				
				setString(pstm, 1, propertyName, query);
	
				long rowNum=executeUpdate(pstm, query);
				
				if (log.isDebugEnabled()){
					log.debug("Lineas actualizadas:"+rowNum);			
				}	
			} finally {
				closeConnection(connection);
			}
		}catch (DaoManagerException e) {
			throw new DaoManagerException(" propertyName:'"+propertyName+"'",e);
		}
	}

	public static final String UPDATE_VALUE="update "+TABLE_NAME+" set VALUE=?1 where NAME=?2";

	public void setValue(String propertyName, String value) throws DaoManagerException {
		try {
			Connection connection = getConnection();
			String query=UPDATE_VALUE;
			
			try {
				PreparedStatement pstm = getPrepareStatement(connection,query);
				
				setString(pstm, 1, value, query);
				setString(pstm, 2, propertyName, query);
	
				long rowNum=executeUpdate(pstm, query);
				
				if (log.isDebugEnabled()){
					log.debug("Lineas actualizadas:"+rowNum);			
				}	
			} finally {
				closeConnection(connection);
			}
		}catch (DaoManagerException e) {
			throw new DaoManagerException( " propertyName:'"+propertyName+
									 "', value:'"+value+"'",e);
		}
	}


	public static final String UPDATE_DESCRIPCION="update "+TABLE_NAME+" set DESCRIPTION=?1 where NAME=?2";

	public void updateDescripcion(String propertyName, String descripcion) throws DaoManagerException {
		try {
			Connection connection = getConnection();
			String query=UPDATE_DESCRIPCION;
			
			try {
				PreparedStatement pstm = getPrepareStatement(connection,query);
				
				setString(pstm, 1, descripcion, query);
				setString(pstm, 2, propertyName, query);
	
				long rowNum=executeUpdate(pstm, query);
				
				if (log.isDebugEnabled()){
					log.debug("Lineas actualizadas:"+rowNum);			
				}	
			} finally {
				closeConnection(connection);
			}
		}catch (DaoManagerException e) {
			throw new DaoManagerException(" propertyName:'"+propertyName+
									"', descripcion:"+descripcion+"'",e);
		}
	}


}
