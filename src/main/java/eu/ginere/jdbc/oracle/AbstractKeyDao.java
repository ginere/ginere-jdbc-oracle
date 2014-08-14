package eu.ginere.jdbc.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.test.TestResult;


public abstract class AbstractKeyDao<I,T extends I> extends AbstractDAO{

	protected static final Logger log = Logger.getLogger(AbstractKeyDao.class);
	
	public final String TABLE_NAME;
	public final String KEY_COLUM_NAME;

	protected final String GET_BY_ID_QUERY;
	protected final String GET_ALL_QUERY_LIMIT;
	
	protected final String GET_ALL_QUERY;
	protected final String GET_ALL_IDS;
	protected final String COUNT_QUERY;
	protected final String DELETE_QUERY;

	protected final String INSERT_QUERY;
	protected final String UPDATE_QUERY;


	protected final String  COLUMNS_MINUS_COLUMN_NAME;
	protected final String  COLUMNS_INCLUDING_COLUMN_NAME;
	
	
	protected AbstractKeyDao(String tableName,
							 String keyColumnName,
							 String columnsArrayMinusKeyColumnName[]){
		this.TABLE_NAME=tableName;
		this.KEY_COLUM_NAME=keyColumnName;
		
		this.COLUMNS_MINUS_COLUMN_NAME=StringUtils.join(columnsArrayMinusKeyColumnName,',');
		this.COLUMNS_INCLUDING_COLUMN_NAME=keyColumnName+','+COLUMNS_MINUS_COLUMN_NAME;


		this.GET_BY_ID_QUERY="SELECT "+COLUMNS_MINUS_COLUMN_NAME+
			" from "+tableName + " WHERE "+keyColumnName+"=? and ROWNUM<=1";
		this.GET_ALL_QUERY="select " + COLUMNS_INCLUDING_COLUMN_NAME+ " from " + tableName+ " ";
		this.GET_ALL_QUERY_LIMIT="select " + COLUMNS_INCLUDING_COLUMN_NAME+ " from " + tableName+ "  WHERE ROWNUM <= ?";
		this.GET_ALL_IDS="SELECT "+keyColumnName+" from "+tableName;
		this.COUNT_QUERY="select count(*) from " + tableName;
		this.DELETE_QUERY="DELETE from " + tableName + " where "+keyColumnName+"=?";
		
		StringBuilder insertBuilder=new StringBuilder();
		insertBuilder.append("INSERT INTO ");
		insertBuilder.append(tableName);
		insertBuilder.append("(");
		insertBuilder.append(COLUMNS_INCLUDING_COLUMN_NAME);
		insertBuilder.append(") VALUES (");

		// First the key column
		insertBuilder.append("?");
		
		// then the rest of the column
		for (int i=0;i<columnsArrayMinusKeyColumnName.length;i++){
			insertBuilder.append(",?");
		}
		insertBuilder.append(")");
		
		this.INSERT_QUERY=insertBuilder.toString();

		StringBuilder updateBuilder=new StringBuilder();
		updateBuilder.append("UPDATE ");
		updateBuilder.append(tableName);
		updateBuilder.append(" set ");

		for (int i=0;i<columnsArrayMinusKeyColumnName.length;i++){
			if (i<columnsArrayMinusKeyColumnName.length-1){
				updateBuilder.append(columnsArrayMinusKeyColumnName[i]);
				updateBuilder.append("=?");
				updateBuilder.append(",");
			} else {
				updateBuilder.append(columnsArrayMinusKeyColumnName[i]);
				updateBuilder.append("=?");
			}
		}
		updateBuilder.append(" WHERE ");
		updateBuilder.append(keyColumnName);
		updateBuilder.append("=?");
				
		this.UPDATE_QUERY=updateBuilder.toString();
				

	}


	public T get(String id) throws DaoManagerException {
		
		Connection connection = getConnection();
		try {
			return get(connection,id);
		} finally {
			closeConnection(connection);
		}
	}

	public T get(Connection connection,String id) throws DaoManagerException {
		String query=GET_BY_ID_QUERY;
		try {
			PreparedStatement pstm = getPrepareStatement(connection, query);
			try {
				setString(pstm, 1, id, query);
				
				ResultSet rset = executeQuery(pstm, query);
				try {
					if (rset.next()) {
						return  createFromResultSet(id,rset);
					} else {
						throw new DaoManagerException("id:'"+id+"'");
					}
				}finally{
					close(rset);
				}
			}finally{
				close(pstm);
			}
		} catch (SQLException e) {
			String error = "id:'" + id + "'";

			throw new DaoManagerException(error, e);
		}
	}

	public boolean exists(String id) throws DaoManagerException {		
		Connection connection = getConnection();
		String query=GET_BY_ID_QUERY;
		try {
			PreparedStatement pstm = getPrepareStatement(connection, query);
			try {
				setString(pstm, 1, id, query);
				
				ResultSet rset = executeQuery(pstm, query);
				try {
					if (rset.next()) {
						return true;
					} else {
						return false;
					}
				} catch (SQLException e) {
					String error = "id:'" + id + "'";
					
					throw new DaoManagerException(error, e);
				}finally{
					close(rset);
				}
			}finally{
				close(pstm);
			}			
		} catch (DaoManagerException e) {
			String error = "id:'" + id + "'";

			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}
	}

	public List<I> getAll () throws DaoManagerException{
		Connection connection=getConnection();
		String query=GET_ALL_QUERY;

		try{
			PreparedStatement pstm = getPrepareStatement(connection,query);			
			try {
				ResultSet rset = executeQuery(pstm,query);
				
				try {
					List<I> list= new ArrayList<I>(rset.getFetchSize());
				
					while (rset.next()){
						T t=createFromResultSet(rset);
						list.add(t);
					}
					return list;
				}catch (SQLException e){
					String error="Query" + query;
					//			log.error(error, e);
					throw new DaoManagerException(error,e);
				}finally{
					close(rset);
				}
			}finally{
				close(pstm);
			}			
		}catch (DaoManagerException e) {
			String error="Query" + query;
			throw new DaoManagerException(error, e);
		}finally{
			closeConnection(connection);
		}
	}
	
	public List<I> getAll (int limit) throws DaoManagerException{
		Connection connection=getConnection();
		String query=GET_ALL_QUERY_LIMIT;

		try{
			PreparedStatement pstm = getPrepareStatement(connection,query);		
			try {
				setInt(pstm, 1, limit, query);
				
				ResultSet rset = executeQuery(pstm,query);
				try {
					List<I> list= new ArrayList<I>(rset.getFetchSize());
					
					while (rset.next()){
						T t=createFromResultSet(rset);
						list.add(t);
					}
					return list;
				}catch (SQLException e){
					String error="Query" + query;
					//			log.error(error, e);
					throw new DaoManagerException(error,e);
				}finally{
					close(rset);
				}

			} finally {
				closeConnection(connection);
			}
		}catch (DaoManagerException e) {
			String error="Query" + query;
			throw new DaoManagerException(error, e);
		}finally{
			closeConnection(connection);
		}
	}

	public List<String> getAllIds () throws DaoManagerException{
		Connection connection=getConnection();
		String query=GET_ALL_IDS;
		try{
			PreparedStatement pstm = getPrepareStatement(connection,query);
			try {
				return getStringList(pstm, query);
			}finally{
				close(pstm);
			}			
		}catch (DaoManagerException e) {
			String error="Query" + query;
//			Log.error(error, e);
			throw new DaoManagerException(error, e);
		}finally{
			closeConnection(connection);
		}
	}

	public long count() throws DaoManagerException{
		Connection connection=getConnection();
		String query=COUNT_QUERY;
		try{
			PreparedStatement pstm = getPrepareStatement(connection,query);
			try {
				return getLongFromQuery(pstm, query, 0);
			}finally{
				close(pstm);
			}
		}catch (DaoManagerException e) {
			String error="Query" + query;
//			Log.error(error, e);
			throw new DaoManagerException(error, e);
		}finally{
			closeConnection(connection);
		}
	}

	public void delete(String id)throws DaoManagerException{
		Connection connection = getConnection();
		String query=DELETE_QUERY;
		try {
			PreparedStatement pstm = getPrepareStatement(connection,
														 query);
			try {
				setString(pstm, 1, id, query);
				
				executeUpdate(pstm, query);
			}finally{
				close(pstm);
			}			
		} catch (DaoManagerException e) {
			String error = "id:'" + id + "' query:"+query;
			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}
	}


	protected String insertFromSecuence(I interf,
										String secuenceName) throws DaoManagerException {
		Connection connection = getConnection();
		String query=INSERT_QUERY;
		
		try {				
			String id = Long.toString(getNextValueFromSecuence(secuenceName));
			
			PreparedStatement pstmInsert = getPrepareStatement(connection,
															   query);
			try {
				setString(pstmInsert,1,id,query);
				
				setInsertStatementFromSequence(pstmInsert,id,interf,query);
				
				executeUpdate(pstmInsert, query);

				return id;
			}finally{
				close(pstmInsert);
			}						
		} catch (DaoManagerException e) {
			String error = "Insert object:'" + interf + 
				"' query:'" + query+
				"'";
			
			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}		
	}


	protected void insertInner(I interf) throws DaoManagerException {
		Connection connection = getConnection();
		String query=INSERT_QUERY;
		
		try {				
			PreparedStatement pstmInsert = getPrepareStatement(connection,
															   query);
			try {
				setInsertStatement(pstmInsert,interf,query);
				
				executeUpdate(pstmInsert, query);
			}finally{
				close(pstmInsert);
			}						
		} catch (DaoManagerException e) {
			String error = "Insert object:'" + interf + 
				"' query:'" + query+
				"'";
			
			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}		
	}

	public void update(I interf) throws DaoManagerException {
		Connection connection = getConnection();
		String query=UPDATE_QUERY;

		try {						
			PreparedStatement pstmInsert = getPrepareStatement(connection,
															   query);
			try {
				setUpdateStatement(pstmInsert,interf,query);
				
				executeUpdate(pstmInsert, query);
			}finally{
				close(pstmInsert);
			}						
		} catch (DaoManagerException e) {
			String error = "Update object:'" + interf + 
				"' query:'" + query+
				"'";
			throw new DaoManagerException(error, e);
		} finally {
			closeConnection(connection);
		}		

	}
	
	public List<I> getByConditions (String conditions) throws DaoManagerException{
		Connection connection=getConnection();
		String query=GET_ALL_QUERY+conditions;

		try{
			PreparedStatement pstm = getPrepareStatement(connection,query);			
			try {
				ResultSet rset = executeQuery(pstm,query);
				try {
					List<I> list= new ArrayList<I>(rset.getFetchSize());
					
					while (rset.next()){
						T t=createFromResultSet(rset);
						list.add(t);
					}
					return list;
				}catch (SQLException e){
					String error="Query" + query;
					throw new DaoManagerException(error,e);
				}finally{
					close(rset);
				}
			}finally{
				close(pstm);
			}					
		}catch (DaoManagerException e) {
			String error="Query" + query;
			throw new DaoManagerException(error, e);
		}finally{
			closeConnection(connection);
		}
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	/**
	 * Tiene que leer todas las columnas menos la columna de la primary key
	 */
	protected abstract T createFromResultSet(String id,ResultSet rset) throws SQLException, DaoManagerException;

	/**
	 * Tiene que leer todas las columnas del objeto
	 */
	protected T createFromResultSet(ResultSet rset) throws SQLException, DaoManagerException{
		String id=getString(rset,KEY_COLUM_NAME,TABLE_NAME);
		
		return createFromResultSet(id, rset);
	}
	
	
	public interface Iterator<T>{

		/**
		 * @param id
		 * @param name
		 * @param folderFullName
		 * @return while this return true, continues working
		 */
		boolean access(T i);
		
	};
	

	public void iterate(Iterator<T> i) throws DaoManagerException {
		
		Connection connection=getConnection();
		String query=GET_ALL_QUERY;

		try{
			PreparedStatement pstm = getPrepareStatement(connection,query);			
			try {
				iterate(i, pstm, query);
				return ;
			}finally{
				close(pstm);
			}							
		}catch (DaoManagerException e) {
			String error="Query" + query;
			throw new DaoManagerException(error, e);
		}finally{
			closeConnection(connection);
		}
	}
	
	public void iterate(Iterator<T> i,PreparedStatement pstm,String query ) throws DaoManagerException {
		ResultSet rset = executeQuery(pstm,query);
		try {
			while (rset.next()){
				T t=createFromResultSet(rset);
				if (!i.access(t)){
					return ;
				}
			}
			return ;
		}catch (SQLException e){
			String error="Query" + query;
			throw new DaoManagerException(error,e);
		}finally{
			close(rset);
		}
	}

	
	
	/**
	 * Vuelca todos los 
	 * @param pstmInsert
	 * @param obj
	 * @param query
	 * @throws DaoManagerException
	 */
	protected abstract void setInsertStatementFromSequence(PreparedStatement pstmInsert,String id,I obj,String query) throws DaoManagerException;
	protected abstract void setInsertStatement(PreparedStatement pstmInsert,I obj,String query) throws DaoManagerException;
	protected abstract void setUpdateStatement(PreparedStatement pstmInsert,I obj,String query) throws DaoManagerException;
	//	protected abstract void setUpdateStatement(PreparedStatement pstmInsert,T obj,String query) throws DaoManagerException;

	protected String getString(ResultSet rset,String columnName,String tableName) throws DaoManagerException{
		try {
			return rset.getString(columnName);
		}catch(SQLException e){
			throw new DaoManagerException("columnName:"+columnName+" tableName:"+tableName,e);
		}
	}
	
	@Override
	public TestResult test() {
		TestResult ret=new TestResult(getClass());
		TestResult parent=super.test();
		
		ret.add(parent);
		
		if (parent.isOK()){
			try {
				getAll(1);
			} catch (DaoManagerException e) {
				ret.addError("While getting one line from database", e);
			}
		} 
		
		return ret;
	}
}
