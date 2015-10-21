package eu.ginere.jdbc.oracle;

import java.sql.PreparedStatement;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.dao.jdbc.KeyDTO;

/**
 * 
 *
 */
public abstract class AbstractKeyDTODao<I extends KeyDTO> extends AbstractKeyDao<I,I>{

	
	protected AbstractKeyDTODao(String tableName,
							 String keyColumnName,
							 String columnsArrayMinusKeyColumnName[]){
		super(tableName,keyColumnName,columnsArrayMinusKeyColumnName);
	}
	

//	/**
//	 * Tiene que leer todas las columnas menos la columna de la primary key
//	 */
//	protected abstract I createFromResultSet(String id,
//											 ResultSet rset,
//											 String query) throws SQLException, DaoManagerException;

	
	/**
	 * Vuelca todos los 
	 * @param pstmInsert
	 * @param obj
	 * @param query
	 * @throws DaoManagerException
	 */
	protected abstract int fillUpdateStatement(PreparedStatement pstmInsert,I obj,int argumentIndex,String query) throws DaoManagerException;

	protected I insert(I obj,String secuenceName) throws DaoManagerException {
		String id=super.insertFromSecuence(obj, secuenceName);
		
		obj.setKey(id);
		
		return obj;
	}
	
	@Override
	protected void setInsertStatementFromSequence(PreparedStatement pstmInsert,
												  String id,
												  I obj,
												  String query) throws DaoManagerException {
		int i=1;

		set(pstmInsert,i++, id, query);

		fillUpdateStatement(pstmInsert,obj,i,query);
	}
	
	@Override
	protected void setUpdateStatement(PreparedStatement pstmInsert,
									  I obj,
									  String query) throws DaoManagerException {
		int i=1;

		i=fillUpdateStatement(pstmInsert,obj,i,query);		
		
		set(pstmInsert,i++,obj.getKey(),query);
	}
	
	
	protected void setInsertStatement(PreparedStatement pstmInsert,I obj,String query) throws DaoManagerException{
		setInsertStatementFromSequence(pstmInsert, obj.getKey(), obj, query);
	}
}
