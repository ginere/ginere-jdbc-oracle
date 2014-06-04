package eu.ginere.jdbc.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * @author ventura
 *
 * Clase madra para todos los objetos producidos por un DAO
 */
public abstract class DTO {
	
	protected String getString(ResultSet rset,String columnName,String tableName) throws DaoManagerException{
		try {
			return rset.getString(columnName);
		}catch(SQLException e){
			throw new DaoManagerException("columnName:"+columnName+" tableName:"+tableName,e);
		}
	}

	protected int getInt(ResultSet rset,String columnName,String tableName) throws DaoManagerException{
		try {
			return rset.getInt(columnName);
		}catch(SQLException e){
			throw new DaoManagerException("columnName:"+columnName+" tableName:"+tableName,e);
		}
	}
	
	protected boolean getBoolean(ResultSet rset,String columnName,String tableName) throws DaoManagerException{
		try {
			return rset.getBoolean(columnName);
		}catch(SQLException e){
			throw new DaoManagerException("columnName:"+columnName+" tableName:"+tableName,e);
		}
	}
	
	protected long getLong(ResultSet rset,String columnName,String tableName) throws DaoManagerException{
		try {
			return rset.getLong(columnName);
		}catch(SQLException e){
			throw new DaoManagerException("columnName:"+columnName+" tableName:"+tableName,e);
		}
	}
	
	protected Date getDate(ResultSet rset,String columnName,String tableName) throws DaoManagerException{
		try {
			return rset.getTimestamp(columnName);
		}catch(SQLException e){
			throw new DaoManagerException("columnName:"+columnName+" tableName:"+tableName,e);
		}
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
