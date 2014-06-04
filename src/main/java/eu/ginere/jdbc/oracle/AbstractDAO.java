package eu.ginere.jdbc.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import eu.ginere.util.test.TestInterface;
import eu.ginere.util.test.TestResult;


public abstract class AbstractDAO implements TestInterface{
	
	private DataBase dataBase=null;
	
	protected AbstractDAO(){
		this.dataBase=DataBase.DEFAULT_DATABASE;		
	}
	
	public void setDataBase(DataBase dataBase){
		this.dataBase=dataBase;		
	}

	protected Connection getConnection() throws DaoManagerException{
		if (dataBase==null){
			throw new DaoManagerException("Data besae connection not initialized");
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
	

	protected String getString(PreparedStatement pstm, String query) throws DaoManagerException{
		return DataBase.getString(pstm,query);
	}

	protected String getString(PreparedStatement pstm, String query,String defaultValue) throws DaoManagerException{
		return DataBase.getString(pstm,query,defaultValue);
	}


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

	protected static long getLongFromQuery(PreparedStatement pstm, String query,
			int defaultValue) throws DaoManagerException {
		return DataBase.getLongFromQuery(pstm, query, defaultValue);
	}


	protected static int getIntFromQuery(PreparedStatement pstm, String query,
			int defaultValue) throws DaoManagerException {
		return DataBase.getIntFromQuery(pstm, query, defaultValue);
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
	

	public TestResult test() {
		
		TestResult ret=new TestResult(AbstractDAO.class);
		
		
		if (dataBase==null){
			ret.addError("The database is null. Not yet defined");
		} else  {
			ret.add(dataBase.test());
		}
				
		return ret;
	}
	
	public List<String> getStringList (String query) throws DaoManagerException{
		if (dataBase==null){
			throw new DaoManagerException("Data besae connection not initialized");
		} else  {
			return dataBase.getStringList(query);
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
			throw new DaoManagerException("Data besae connection not initialized");
		} else  {
			 dataBase.startThreadLocal();
		}		
	}


	public void endThreadLocal(boolean forzeClean) throws DaoManagerException {
		if (dataBase==null){
			throw new DaoManagerException("Data besae connection not initialized");
		} else  {
			DataBase.endThreadLocal(forzeClean);
		}
	}

}
