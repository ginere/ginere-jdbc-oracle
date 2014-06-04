package eu.ginere.service.configuration;

import java.util.Hashtable;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.ginere.jdbc.oracle.DaoManagerException;
import eu.ginere.jdbc.oracle.configuration.dao.ConfigurationDAOInterface;
import eu.ginere.util.test.TestInterface;
import eu.ginere.util.test.TestResult;

/**
 * Clase para la configuracion del servicio.
 *
 */

public class ConfigurationService implements TestInterface {

	static final Logger log = Logger.getLogger(ConfigurationService.class);

	/** DAO del ConfigurationService. */
	private static ConfigurationDAOInterface DAO;
	
	/**
	 * Obtiene el DAO del ConfigurationService.
	 * @return ConfigurationDAOInterface.
	 */
	private static ConfigurationDAOInterface getDao() {
		if (DAO==null){
			throw new IllegalAccessError("No se ha iniciado el dao para el servicio de configuracion en base de datos");
		} else {
			return DAO;
		}
	}
	
	/**
	 * Inicilaliza el DAO del ConfigurationService.
	 * @param dao DAO de la Configuracion.
	 */
	public static void init(ConfigurationDAOInterface dao){
		DAO=dao;
	}
	
	/** Estructura de datos del ConfigurationService. */
	private static final Hashtable<String, String> CACHE=new Hashtable<String, String>();

//	/**
//	 * @param database
//	 * @throws DaoManagerException
//	 */
//	public static void init(String persistenceUnitName) throws DaoManagerException{
//		if (database == null){
//			throw new DaoManagerException("la database es nula");
//		} else {
//			ConfiguracionDAO.DAO.setDataBase(database);
//			
//			if (!database.testConnection()){
//				log.error("No hat conexion a la database:"+database.getName());
//			}
//		}		
//	}

	/**
	 * Realiza el testeo de la clase.
	 * @return String con el resultado de la prueba.
	 */
	public TestResult test() {
		TestResult ret=new TestResult(ConfigurationService.class);
		
		if (DAO==null){
			ret.addError("No se ha iniciado el dao para el servicio de configuracion en base de datos");
		} else {
			ret.add(getDao().test());
		}
		
		return ret;
	}

	/**
	 * Realiza la limpieza del cache.
	 * @return int con el resultado de la limpieza.
	 */
	public static int clearCache() {
		int size=CACHE.size();
		
		CACHE.clear();
		
		return size;
	}

	/**
     * Devuelve una property existente en BD.
     * 
     * @param propertyName String El nombre de la propiedad.
     * @return String El valor de la propiedad.
     * @throws ExamException La excepcion lanzada.
     */
	public static String getProperty(String propertyName) throws DaoManagerException {
		if (CACHE.containsKey(propertyName)){
			return CACHE.get(propertyName);
		} else {
			String ret=getDao().getProperty(propertyName);
			if (ret!=null){
				CACHE.put(propertyName,ret);
			}
			return ret;
		}
	}
	

	/**
     * Devuelve una propiedad existente en BD , o el valor por defecto si la encuentra.
     * 
     * @param propertyName String El nombre de la propiedad.
     * @param defaultValue String El valor por defecto.
     * @return String El valor de la propiedad.
     */
	public static String getProperty(String propertyName,String defaultValue) {
		if (existsProperty(propertyName)){
			try {
				String ret=getProperty(propertyName);
				
				return ret;
			}catch (DaoManagerException e){
				log.warn("Obteniendo la propiedad :"+propertyName+"'",e);
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
    }

	/**
     * Crea una propiedad con el valor por defecto si no la encuentra.
     * 
     * @param propertyName String El nombre de la propiedad.
     * @param defaultValue String El valor por defecto.
     * @param description String La descripcion de la propiedad.
     */
	public static void defineProperty(String propertyName,String defaultValue,String description) {
        try {
            if (!getDao().exists(propertyName)){
                getDao().insertProperty(propertyName,defaultValue,description);
                CACHE.put(propertyName,defaultValue);
            } else {
                log.debug("La propiedad:'"+propertyName+"' ya existe en base de datos");
                getDao().updateDescripcion(propertyName,description);
            }
        }catch (DaoManagerException e) {
            log.fatal("propertyName:'"+propertyName
                    +"' defaultValue:'"+defaultValue
                    +"' description:'"+description
                    +"'"
                    ,e);
        }
    }
    
	/**
     * Asigna un valora una propiedad.
     * 
     * @param propertyName String El nombre de la propiedad.
     * @param value String El valor asignado.
     */
    public static void setValue(String propertyName,String value) {
        try {
            if (!getDao().exists(propertyName)){
                getDao().insertProperty(propertyName,value,"Generado automaticamente.");
                CACHE.put(propertyName,value);
            } else {
                getDao().setValue(propertyName,value);
                CACHE.put(propertyName,value);
            }
        }catch (DaoManagerException e) {
            log.fatal("propertyName:'"+propertyName+
                    "' value:'"+value+
                    "'"
                    ,e);
        }
    }

	/**
	 * Comprueba la existencia de una property en BD.
	 * @param  propertyName String El nombre de la propiedad.
	 * @return TRUE si existe, FALSE si no.
	 */
	public static boolean existsProperty(String propertyName) {
		if (CACHE.containsKey(propertyName)){
			return true;
		} else {
			try {
				return getDao().exists(propertyName);
			} catch (DaoManagerException e) {
				log.error("Propiedad:"+propertyName);
				
				return false;
			}
		}
	}

	 /**
     * Devuelve un array de valores asociado a una property del fichero. Para
     * separalas, se usa el caracter "," .
     * 
     * @param propertyName String El nombre de la propiedad.
     * @param defaultValue String[] Serie de valores por defecto.
     * @return String[] Serie de valores obtenidos.
     */
    public static String[] getPropertyStringArray(final String propertyName, final String defaultValue[]) {
        try {
            final String value = getProperty(propertyName);
            String[] resultado;
            
            if (value == null){
            	resultado=  ArrayUtils.EMPTY_STRING_ARRAY;
            } else {
                resultado = StringUtils.split(value, ',');
                for (int i = 0; i < resultado.length; i++) {
                    resultado[i] = resultado[i].trim();
                }
            }

            return resultado;
        } catch (DaoManagerException e) {
        	log.info("Obteniendo el valor array para la propiedad:'"+propertyName+"'",e);
            return defaultValue;
        }
    }

    /**
     * Devuelve una property parseada a booleano (true o false). En caso de no
     * poder parsearlo, devuelve el valor por defecto.
     * 
     * @param propertyName String El nombre de la propiedad.
     * @param defaultValue BOOLEAN El booleano por defecto.
     * @return TRUE si existe, FALSE si no.
     */
    public static Boolean getBooleanProperty(final String propertyName, final Boolean defaultValue) {
        final String value = getProperty(propertyName, null);
        if (value != null) {
            try {
                return Boolean.parseBoolean(value);
            } catch (final NumberFormatException e) {
				if (log.isDebugEnabled()){
					log.debug("No se pudo parsear el valor'" + value + "' para la propiedad:'" + propertyName
							  + "', devolviendo el valor por defecto:" + defaultValue, e);
				}
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Devuelve una property parseada a int con el nombre propertyName. En caso
     * de no poder parsear o devolverla, devuelve el valor por defecto
     * defaultValue.
     * 
     * @param propertyName String El nombre de la propiedad.
     * @param defaultValue int El entero por defecto.
     * @return int El entero de devoluciï¿½n.
     */
    public static int getIntProperty(final String propertyName, final int defaultValue) {
		try {
			final int value = getIntProperty(propertyName);
			return value;
		} catch (DaoManagerException e){
			if (log.isDebugEnabled()){
				log.debug("propertyName '" + propertyName + "', defaultValue" + defaultValue, e);
			}
			return defaultValue;			
		}
    }

    /**
     * Devuelve una property parseada a entero. En caso de no poder parsear,
     * devuelve una excepcion DaoManagerException.
     * 
     * @param propertyName String El nombre de la propiedad.
     * @return int El entero de devoluciï¿½n.
     * @throws DaoManagerException La excepcion lanzada.
     */
    public static int getIntProperty(final String propertyName) throws DaoManagerException {
        final String value = getProperty(propertyName);
		
		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException e) {
			throw new DaoManagerException("Error parseando el valor'" + value + "' para la propiedad:'"
										  + propertyName + "'", e);
		}
    }

    /**
     * Indica si una property de tipo int con el nombre propertyName existe en
     * el fichero.
     * 
     * @param propertyName String El nombre de la propiedad.
     * @return TRUE si existe, FALSE si no.
     */
    public static boolean existIntProperty(final String propertyName) {
        try {
            getIntProperty(propertyName);
			
            return true;
        } catch (final DaoManagerException e) {
            return false;
        }
    }
}
