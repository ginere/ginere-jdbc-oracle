package eu.ginere.jdbc.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * @author ventura
 */
public class ThreadLocalConection {
	static final Logger log = Logger.getLogger(ThreadLocalConection.class);
	
	public static boolean GLOBAL_USE_THREADLOCAL_CONECTION=false;
	
	/**
	 * The ThreadLocal Object 
	 */
	private static ThreadLocal<ThreadLocalConection> threadLocalConection=new ThreadLocal<ThreadLocalConection>();

	
	/**
	 * Se almacenan los datos para cada database a las que se conectan. 
	 * 
	 * @author ventura
	 */
	private class DataBaseStuff {
		DataBase database=null;
		Connection connection=null;
		Hashtable<String, PreparedStatement> statementCached=null;

		public DataBaseStuff(DataBase database){
			this.database=database;
		}

		Connection getConnection() throws DaoManagerException{
			if (connection==null){
				connection=getConnectionInner(database.getDataSource());
				cleared=false;
			}

			return connection;
		}	
		
		public void clean() {
			closeInner(connection);
			connection=null;
			statementCached=null;
		}
		
		private Hashtable<String, PreparedStatement> getStatementCachedThreadLocal() {
			if (statementCached == null){
				statementCached=new Hashtable<String, PreparedStatement>();
			}

			return statementCached;
		}
	}


	
	boolean useThreadLocal=false;
	boolean cleared=true;
	private final Hashtable<String, DataBaseStuff> dataBaseCache=new Hashtable<String, DataBaseStuff>();
	
	public ThreadLocalConection(){		
	}
	
	/**
	 * Return the thread local object
	 * @return
	 */
	private static ThreadLocalConection getThreadlocal(){
		ThreadLocalConection ret= threadLocalConection.get();

		if (ret==null){
			ret = new ThreadLocalConection();
			threadLocalConection.set(ret);
		}

		return ret;
	}


	/**
	 * Returns the stuff related to this database or create a new one
	 * if it does not exists
	 * 
	 * @param dataBase
	 * @return
	 */
	private DataBaseStuff getDatabase(DataBase dataBase) {
		DataBaseStuff ret=dataBaseCache.get(dataBase.getName());
		
		if (ret==null){
			ret=new DataBaseStuff(dataBase);
			dataBaseCache.put(dataBase.getName(), ret);
		}
		
		return ret;
	}
	
	private void startThreadLocalPrivate() {
		useThreadLocal=true;
	}

	private void endThreadLocalPrivate(boolean forzeClear) {
		useThreadLocal=false;
		if (forzeClear){
			clean();
		}
	}

	static private boolean useThreadLocal(){
		if (GLOBAL_USE_THREADLOCAL_CONECTION){
			return true;
		} else {
			return getThreadlocal().useThreadLocal;
		}
	}
	
	/**
	 * This clean the thread local stuff
	 */
	private void clean() {
		if (!cleared){
			long time = 0;

			if (log.isInfoEnabled()) {
				time = System.currentTimeMillis();
			}
			
			for (Map.Entry<String,DataBaseStuff>entry:dataBaseCache.entrySet()){
				DataBaseStuff value=entry.getValue();
				value.clean();
			}
			dataBaseCache.clear();
			
//			closeInner(connection);
//			connection=null;
//			statementCached=null;
			
			if (log.isInfoEnabled()) {
				log.info("Thread local cleared in:"
							 + (System.currentTimeMillis() - time) + " mill");
			}
			cleared=true;
		}
	}

	/**
	 * 
	 * The private stuff
	 * 
	 */
	
	
	private Connection getConnectionPrivate(DataBase dataBase) throws DaoManagerException{
		DataBaseStuff database=getDatabase(dataBase);
		
		return database.getConnection();
	}

	private void closePrivate(Connection connection) {
		return;
	}

	private void closePrivate(PreparedStatement pstm) {
			return;
//		// Si hay muchas queries no es puede mantener abiertos los PSTM
//		closeInner(pstm);
	}

	private Hashtable<String, PreparedStatement> getStatementCachedThreadLocal(DataBase dataBase) {
		DataBaseStuff database=getDatabase(dataBase);
		return database.getStatementCachedThreadLocal();
	}

	private PreparedStatement getPrepareStatementPrivate(DataBase dataBase,
														 Connection connection, 
														 String query) throws DaoManagerException {
		Hashtable<String, PreparedStatement> statementCached=getStatementCachedThreadLocal(dataBase);

		PreparedStatement ret=statementCached.get(query);
		
		if (ret==null){
			ret=getPrepareStatementInner(connection,query);
			statementCached.put(query,ret);
		}
				
		return ret;	
//		
//		return getPrepareStatementInner(connection,query);
	}



	/**
	 * 
	 * Inner stuff: Here we do the jdb actions
	 * 
	 *  
	 */


	static private Connection getConnectionInner(DataSource dataSource) throws DaoManagerException {
		if (dataSource == null) {
			throw new DaoManagerException("La datasource es null, lo mas probable es que no se haya inicializado correctamente");
		} else {
			try {
				long time = 0;
					
				if (log.isInfoEnabled()) {
					time = System.currentTimeMillis();
				}
				Connection ret = dataSource.getConnection();
				ret.setAutoCommit(true);
				if (log.isInfoEnabled()) {
					log.info("Connection obtained in:"
							 + (System.currentTimeMillis() - time) + " mill");
				}
				
				return ret;
			} catch (SQLException e) {
				throw new DaoManagerException("Obteniendo la conexion", e);
			}
		}
	}
	
	static protected void closeInner(Connection connection) {
		try {
			long time = 0;
			
			if (log.isInfoEnabled()) {
					time = System.currentTimeMillis();
			}
			connection.close();
			
			if (log.isInfoEnabled()) {
				log.info("Connection Closed in:"
						 + (System.currentTimeMillis() - time) + " mill");
			}
		} catch (SQLException e) {
			log.warn("Clossing conection", e);
		}
	}

	static protected void closeInner(PreparedStatement pstm) {
		try {
			pstm.close();
		} catch (SQLException e) {
			log.warn("Clossing conection", e);
		}
	}

	private static PreparedStatement getPrepareStatementInner(Connection connection,String query) throws DaoManagerException {
		try {
			return connection.prepareStatement(query);
		} catch (SQLException e) {
			throw new DaoManagerException("Query:'" + query	+ "'", e);
		}
	}
	
	
	
	
	
	
	
	/**
	 *
	 * Public stuff
	 * 
	 */
	
	
	

	public static Connection getConnection(DataBase database) throws DaoManagerException{
		if (useThreadLocal()){
			return getThreadlocal().getConnectionPrivate(database);
		} else {
			getThreadlocal().clean();
			return getConnectionInner(database.getDataSource());
		}
	}

	protected static PreparedStatement getPrepareStatement(DataBase dataBase,Connection connection,String query) throws DaoManagerException {
		if (useThreadLocal()){
			return getThreadlocal().getPrepareStatementPrivate(dataBase,connection,query);
		} else {
			return getPrepareStatementInner(connection,query);
		}
	}

	public static void close(Connection connection){
		if (useThreadLocal()){
			getThreadlocal().closePrivate(connection);
		} else {
			closeInner(connection);
		}
	}

	public static void close(PreparedStatement pstm){
		if (useThreadLocal()){
			getThreadlocal().closePrivate(pstm);
		} else {
			closeInner(pstm);
		}
	}

	public static void startThreadLocal() {
		getThreadlocal().startThreadLocalPrivate();
	}


	public static void endThreadLocal(boolean forzeClear) {
		getThreadlocal().endThreadLocalPrivate(forzeClear);
	}

}

