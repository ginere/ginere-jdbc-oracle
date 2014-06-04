package eu.ginere.jdbc.enumeration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.ginere.jdbc.oracle.DaoManagerException;


public abstract class SQLEnum {

	private final String id;
	private final String name;
	private final String description;
	private final Class<? extends SQLEnum> clazz;

	static private final Hashtable<Class<? extends SQLEnum>,List<SQLEnum>> valuesList=new Hashtable<Class<? extends SQLEnum>,List<SQLEnum>>();

	static private final Hashtable<Class<? extends SQLEnum>,Map<String,SQLEnum>> valuesCache=new Hashtable<Class<? extends SQLEnum>,Map<String,SQLEnum>>();

	protected SQLEnum(String id,String name,String description,Class<? extends SQLEnum> clazz){
		this.id=id;
		this.name=name;
		this.description=description;
		this.clazz=clazz;

		init();
	}


	protected SQLEnum(String id,String name,String description){
		this.id=id;
		this.name=name;
		this.description=description;
		this.clazz=this.getClass();
		
		init();
	}
	
	private void init(){		
		synchronized(valuesList){
			if (!valuesList.containsKey(clazz)){
				List <SQLEnum>list=new ArrayList<SQLEnum>();
				valuesList.put(clazz,list);
				
				Map<String,SQLEnum> map=new Hashtable<String,SQLEnum>();
				valuesCache.put(clazz,map);
			}
		}
		
		Map<String,SQLEnum> map=valuesCache.get(clazz);
		if (!map.containsKey(id)){
			map.put(id,this);	
			
			List <SQLEnum> list=valuesList.get(clazz);
			list.add(this);
		} 
	}
	
	protected void delete(){		
		List <SQLEnum> list=valuesList.get(clazz);
		for (Iterator<SQLEnum> iterator=list.iterator();iterator.hasNext();){	
			SQLEnum enumVal=iterator.next();
			
			if (id.equals(enumVal.getId())){
				iterator.remove();
			}
		}
		

		
		Map<String,SQLEnum> map=valuesCache.get(clazz);
		map.remove(id);		
	}

	public String getId() {
		return id;
	}

	public String getName() {
//		return I18NConnector.getLabel(clazz,name);
		return name;
	}

	public String getDescription() {
//		return I18NConnector.getLabel(clazz,description);
		return description;
	}

	public String toString() {
		return id;
	}
	
	public static SQLEnum value(Class<? extends SQLEnum> clazz,String value) {
		if (value == null) {
			return null;
		}
		
		value=value.trim();
		
		if (valuesCache.containsKey(clazz)){
			Map<String,SQLEnum> map2=valuesCache.get(clazz);
			
			if (map2.containsKey(value)){
				return map2.get(value);
			}
		}

		return null;
	}

	public static SQLEnum value(Class<? extends SQLEnum> clazz,ResultSet rset,String colName) throws DaoManagerException {
		try {
			String value=rset.getString(colName);
	
			return value(clazz,value);
		}catch(SQLException e){
			throw new DaoManagerException("Class:'"+clazz+"'colName:'"+colName+"'",e);
		}
	}

	public static List<SQLEnum> values(Class<? extends SQLEnum> clazz) {
		if (valuesList.containsKey(clazz)){
			return valuesList.get(clazz);
		} else {
			return null;
		}
	}
}
