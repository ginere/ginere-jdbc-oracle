package eu.ginere.jdbc.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.test.TestInterface;
import eu.ginere.base.util.test.TestResult;



public abstract class AbstractDAO implements TestInterface{
	static final Logger log = Logger.getLogger(AbstractDAO.class);
	
	private DataBase dataBase=null;

	public final String TABLE_NAME;
	
	private final String COUNT ;

	protected final String DELETE_ALL_QUERY;
	protected final String TEST_QUERY;
	
	protected AbstractDAO(String tableName){
		this.dataBase=DataBase.DEFAULT_DATABASE;

		this.TABLE_NAME=tableName;
		this.COUNT = "select COUNT(*) from " + tableName+ " WHERE ROWNUM<=1";
		this.DELETE_ALL_QUERY="DELETE from " + tableName ;
		this.TEST_QUERY = "select * from " + tableName+ " WHERE ROWNUM<=1";


	}
	
	public void setDataBase(DataBase dataBase){
		this.dataBase=dataBase;		
	}

	protected Connection getConnection() throws DaoManagerException{
		if (dataBase==null){
			throw new DaoManagerException("Data base connection not initialized");
		} else  {
			return dataBase.getConnection();
		}
	}	

	protected void closeConnection(Connection connection) throws DaoManagerException{
		DataBase.closeConnection(connection);
	}
	
	protected void close(ResultSet rset) throws DaoManagerException{
		DataBase.close(rset);
	}
	
	protected void close(PreparedStatement pstm) throws DaoManagerException{
		DataBase.close(pstm);
	}
	

	public long count() throws DaoManagerException{
		return getLongFromQuery(COUNT, 0);
	}
	
	public void deleteFromOneColmunQuery(String query,String value)throws DaoManagerException{
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection,
														 query);
            try {
                setString(pstm, 1, value, query);
                executeUpdate(pstm, query);
            }finally {
                close(pstm);
            }            
		} catch (DaoManagerException e) {
			String error = "query:"+query;
			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}
	}
	
	public void deleteFromTwoColmunQuery(String query,Object arg1,Object arg2)throws DaoManagerException{
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection,
														 query);
            try {
                set(pstm, 1, arg1, query);
                set(pstm, 2, arg2, query);

                executeUpdate(pstm, query);
            }finally {
                close(pstm);
            }            
		} catch (DaoManagerException e) {
			String error = "query:"+query;
			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}
	}

	public void deleteAll()throws DaoManagerException{
		Connection connection = getConnection();
		String query=DELETE_ALL_QUERY;
		try {
			PreparedStatement pstm = getPrepareStatement(connection,
														 query);
			
            try {
                executeUpdate(pstm, query);
            }finally {
                close(pstm);
            }
		} catch (DaoManagerException e) {
			String error = "query:"+query;
			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}
	}
	
	
	/**
	 * Upgade or crete the backen. If version <0 this creates the backend
	 */
	protected synchronized void truncate() throws DaoManagerException{
		Connection connection = getConnection();
		String query="TRUNCATE TABLE "+TABLE_NAME;
		
		try {
			PreparedStatement pstm = getPrepareStatement(connection,query);
			try {
				if (log.isInfoEnabled()){
					log.info("Starting truncate Table:"+TABLE_NAME+".");
				}
				
				executeUpdate(pstm, query);
				log.error("Table:"+TABLE_NAME+" truncated");
			}finally{
				close(pstm);
			}
		} catch (DaoManagerException e) {
			String error = "Truncate table:'"+TABLE_NAME+"'";
			
			log.error(error, e);
			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}		
	}


	protected long getLongFromQuery(String query,
									long defaultValue) throws DaoManagerException {
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection, query);

            try {
            	return getLongFromQuery(pstm, query, defaultValue);
            }finally{
                close(pstm);
            }
		} catch (DaoManagerException e) {
			String error = "query:'"+query+"' defaultValue:'" + defaultValue + "'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}
	}

	protected long getLongOneColumnQuery(String query,
										 Object arg,
										 long defaultValue) throws DaoManagerException {
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection, query);
			set(pstm, 1, arg, query);
			
            try {
            	return getLongFromQuery(pstm, query, defaultValue);
            }finally{
                close(pstm);
            }
		} catch (DaoManagerException e) {
			String error = "query:'"+query+"' defaultValue:'" + defaultValue + "'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}
	}
	
	

	protected long getLongTwoColumnQuery(String query,
										 Object arg1,
										 Object arg2,
										 long defaultValue) throws DaoManagerException {
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection, query);
			set(pstm, 1, arg1, query);
			set(pstm, 2, arg2, query);
			
            try {
            	return getLongFromQuery(pstm, query, defaultValue);
            }finally{
                close(pstm);
            }
		} catch (DaoManagerException e) {
			String error = "query:'"+query+"' defaultValue:'" + defaultValue + "'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}
	}
	
	
	protected String getString(PreparedStatement pstm, String query) throws DaoManagerException{
		return DataBase.getString(pstm,query);
	}

	protected Date getDate(PreparedStatement pstm, String query) throws DaoManagerException{
		return DataBase.getDate(pstm,query);
	}

//	protected String getString(PreparedStatement pstm, String defaultValue,String query) throws DaoManagerException{
//		return DataBase.getString(pstm,defaultValue,query);
//	}


	protected boolean hasResult(PreparedStatement pstm, String query) throws DaoManagerException{
		return DataBase.hasResult(pstm,query);
	}
	
	protected void setInt(PreparedStatement pstm,int poss,int value,String query) throws DaoManagerException {
		DataBase.setInt(pstm, poss, value, query);
	}

	protected ResultSet executeQuery(PreparedStatement pstm, String query) throws DaoManagerException{
		return DataBase.executeQuery(pstm,query);
	}

//	protected long getNextValueFromSecuence(Connection connection,String sequenceName)throws DaoManagerException {
//		return DataBase.getNextValueFromSecuence(connection,sequenceName);
//	}
	
	protected long getNextValueFromSecuence(String sequenceName)throws DaoManagerException {
		return dataBase.getNextValueFromSecuence(sequenceName);
	}

	protected static long getLongFromQuery(PreparedStatement pstm, 
											String query,
											long defaultValue) throws DaoManagerException {
		return DataBase.getLongFromQuery(pstm, query, defaultValue);
	}


	protected static int getIntFromQuery(PreparedStatement pstm, String query,
			int defaultValue) throws DaoManagerException {
		return DataBase.getIntFromQuery(pstm, query, defaultValue);
	}
	
	public long executeUpdate(String query) throws DaoManagerException {
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection,query);
			try {
				return executeUpdate(pstm, query);
			}finally{
				close(pstm);
			}
		} finally {
			closeConnection(connection);
		}	
	}
	
	public long executeUpdate(String query,Object arg1) throws DaoManagerException {
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection,query);
			set(pstm, 1, arg1, query);
			try {
				return executeUpdate(pstm, query);
			}finally{
				close(pstm);
			}
		} finally {
			closeConnection(connection);
		}	
	}
	
	public long executeUpdate(String query,Object arg1,Object arg2) throws DaoManagerException {
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection,query);
			set(pstm, 1, arg1, query);
			set(pstm, 2, arg2, query);
			try {
				return executeUpdate(pstm, query);
			}finally{
				close(pstm);
			}
		} finally {
			closeConnection(connection);
		}	
	}
	
	/**
	 * Ejecuta una query.
	 * 
	 * @param pstm
	 * @param query
	 * @return
	 * @throws DaoManagerException
	 */
	public static long executeUpdate(PreparedStatement pstm, String query) throws DaoManagerException {
		return DataBase.executeUpdate(pstm,query);
	}

	protected void setString(PreparedStatement pstm,
						   int i,
						   String value,
						   String query) throws DaoManagerException{
		DataBase.setString(pstm,i,value,query);
	}

	protected void setDate(PreparedStatement pstm,
			   int i,
			   Date value,
			   String query) throws DaoManagerException{
		DataBase.setDate(pstm,i,value,query);
	}
	
	protected PreparedStatement getPrepareStatement(Connection connection,
												  String query) throws DaoManagerException{
		return dataBase.getPrepareStatement(connection,query);
	}
	
	public boolean existsTable(){
		try {
			hasNext(TEST_QUERY);
			return true;
		} catch (Exception e) {
			log.warn("The table does not exists:"+TEST_QUERY,e);
			return false;
		}
	}

	public TestResult test() {
		
		TestResult ret=new TestResult(AbstractDAO.class);
		
		
		if (dataBase==null){
			ret.addError("The database is null. Not yet defined");
		} else  {
			ret.add(dataBase.test());
		}

		try {
			hasNext(TEST_QUERY);
		} catch (Exception e) {
			ret.addError("Connection test error", e);
		}
								
		return ret;
	}
	
	protected static String getStringFromQuery(PreparedStatement pstm, 
                                               String query,
                                               String defaultValue) throws DaoManagerException {
		return DataBase.getString(pstm,defaultValue,query);
	}

	public String getStringFromQuery(String query,String defaultValue) throws DaoManagerException {
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection, query);
			
            try {
        		return DataBase.getString(pstm,defaultValue,query);
            }finally{
                close(pstm);
            }
		} catch (DaoManagerException e) {
			String error = "query:'"+query+
						"'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}		
	}

	public String getStringFromOneArgs(String query,Object arg1,String defaultValue) throws DaoManagerException {
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection, query);
			set(pstm, 1, arg1, query);
			
            try {
        		return DataBase.getString(pstm,defaultValue,query);
            }finally{
                close(pstm);
            }
		} catch (DaoManagerException e) {
			String error = "query:'"+query+
						"' arg1:'" + arg1 + 
						"'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}		
	}
	
	public String getStringFromTwoArgs(String query,Object arg1,String arg2,String defaultValue) throws DaoManagerException {
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection, query);
			set(pstm, 1, arg1, query);
			set(pstm, 2, arg2, query);
			
            try {
        		return DataBase.getString(pstm,defaultValue,query);
            }finally{
                close(pstm);
            }
		} catch (DaoManagerException e) {
			String error = "query:'"+query+
						"' arg1:'" + arg1 + 
						"' arg2:'" + arg2 + 
						"'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}		
	}
	
	public String getStringFromThreeArgs(String query,Object arg1,String arg2, Object arg3,String defaultValue) throws DaoManagerException {
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection, query);
			set(pstm, 1, arg1, query);
			set(pstm, 2, arg2, query);
			set(pstm, 3, arg3, query);
			
            try {
        		return DataBase.getString(pstm,defaultValue,query);
            }finally{
                close(pstm);
            }
		} catch (DaoManagerException e) {
			String error = "query:'"+query+
						"' arg1:'" + arg1 + 
						"' arg2:'" + arg2 + 
						"' arg3:'" + arg3 + 
						"'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}		
	}
	

	public boolean hasNextForOneColmunQuery(String query,String value) throws DaoManagerException {		
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection, query);

            try {
                setString(pstm, 1, value, query);
                
                return hasNext(pstm, query);
            }finally{
                close(pstm);
            }
		} catch (DaoManagerException e) {
			String error = "query:'"+query+"' value:'" + value + "'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}
	}
	
	public boolean hasNext(String query) throws DaoManagerException {		
		Connection connection = getConnection();
		try {
			PreparedStatement pstm = getPrepareStatement(connection, query);
            try {
                return hasNext(pstm, query);
            }finally{
                close(pstm);
            }
		} catch (DaoManagerException e) {
			String error = "query:'"+query+"'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * Usefull to test is an element exits on one table without getting the values
	 * 
	 * @param pstm
	 * @param query
	 * @return true si la query produce algun resultado, false si no.
	 * @throws DaoManagerException
	 */
	protected boolean hasNext(PreparedStatement pstm, String query) throws DaoManagerException {
		if (dataBase==null){
			throw new DaoManagerException("Data base connection not initialized");
		} else  {
			return DataBase.hasResult(pstm, query);
		}
	}

	public List<String> getStringList(String query) throws DaoManagerException{
		if (dataBase==null){
			throw new DaoManagerException("Database connection not initialized");
		} else  {
			return dataBase.getStringList(query);
		}
	}
	
	public List<String> getStringList(String query,Object arg1) throws DaoManagerException{
		Connection connection = getConnection();
		try{
			PreparedStatement pstm = getPrepareStatement(connection,query);
			set(pstm, 1, arg1, query);
			
			try {
				return getStringList(pstm, query);
			}finally{
				close(pstm);
			}		
		} catch (DaoManagerException e) {
			String error = "query:'"+query+
						"' arg1:'" + arg1 + 
						"'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}		
	}
	
	public List<String> getStringList(String query,Object arg1,Object arg2) throws DaoManagerException{
		Connection connection = getConnection();
		try{
			PreparedStatement pstm = getPrepareStatement(connection,query);
			set(pstm, 1, arg1, query);
			set(pstm, 2, arg2, query);
			
			try {
				return getStringList(pstm, query);
			}finally{
				close(pstm);
			}		
		} catch (DaoManagerException e) {
			String error = "query:'"+query+
						"' arg1:'" + arg1 + 
						"' arg2:'" + arg2 + 
						"'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}		
	}
	
	public List<String> getStringList(String query,Object arg1,Object arg2,Object arg3) throws DaoManagerException{
		Connection connection = getConnection();
		try{
			PreparedStatement pstm = getPrepareStatement(connection,query);
			set(pstm, 1, arg1, query);
			set(pstm, 2, arg2, query);
			set(pstm, 3, arg3, query);
			
			try {
				return getStringList(pstm, query);
			}finally{
				close(pstm);
			}		
		} catch (DaoManagerException e) {
			String error = "query:'"+query+
						"' arg1:'" + arg1 + 
						"' arg2:'" + arg2 + 
						"' arg3:'" + arg3 + 
						"'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}		
	}
	
	public List<String> getStringList(PreparedStatement pstm,
											 String query) throws DaoManagerException {
		return DataBase.getStringList(pstm, query);
	}
	
	protected static void set(PreparedStatement pstm,int poss,Object value,String query) throws DaoManagerException {
		DataBase.set( pstm, poss, value, query) ;
	}
	

	public void startThreadLocal() throws DaoManagerException{
		if (dataBase==null){
			throw new DaoManagerException("Data base connection not initialized");
		} else  {
			 dataBase.startThreadLocal();
		}		
	}


	public void endThreadLocal(boolean forzeClean) throws DaoManagerException {
		if (dataBase==null){
			throw new DaoManagerException("Data base connection not initialized");
		} else  {
			DataBase.endThreadLocal(forzeClean);
		}
	}


	//
	// Result set methos
	//

	public static String getString(ResultSet rset, String columnName,String query) throws DaoManagerException {
		try {
			return rset.getString(columnName);
		} catch (SQLException e) {
			throw new DaoManagerException("Query:'"+query+"' columnName:'" + columnName	+ "'", e);
		}
	}

	/**
	 * If the result is null this retuns EMPTY_STRING_ARRAY
	 */
	public static int getInt(ResultSet rset, String columnName,String query) throws DaoManagerException {
		try {
			return rset.getInt(columnName);
		} catch (SQLException e) {
			throw new DaoManagerException("Query:'"+query+"' columnName:'" + columnName	+ "'", e);
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

	public static short getShort(ResultSet rset, String columnName,String query) throws DaoManagerException {
		try {
			return rset.getShort(columnName);
		} catch (SQLException e) {
			throw new DaoManagerException("Query:'"+query+"' columnName:'" + columnName	+ "'", e);
		}
	}
	

	public static byte getByte(ResultSet rset, String columnName,String query) throws DaoManagerException {
		try {
			return rset.getByte(columnName);
		} catch (SQLException e) {
			throw new DaoManagerException("Query:'"+query+"' columnName:'" + columnName	+ "'", e);
		}
	}
		
	public static Timestamp getTimestamp(ResultSet rset, String columnName,String query) throws DaoManagerException {
		try {
			return rset.getTimestamp(columnName);
		} catch (SQLException e) {
			throw new DaoManagerException("Query:'"+query+"' columnName:'" + columnName	+ "'", e);
		}
	}

}
