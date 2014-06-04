package eu.ginere.jdbc.oracle;




/**
 * @author ventura
 *
 * Clase madra para todos los objetos producidos por un DAO
 */
public abstract class KeyDTO extends DTO{

	protected String key;
	
//	protected KeyDTO (ResultSet rset,String keyColumn,String tableName) throws DaoManagerException{
//		this.key=getString(rset,keyColumn,tableName);
//	}
	
	protected KeyDTO(){
		
	}

	protected KeyDTO (String id) {
		this.key=id;
	}

	public String getKey(){
		return key;
	}
	
	public void setKey(String key){
		this.key=key;
	}

}
