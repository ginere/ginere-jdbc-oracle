package eu.ginere.jdbc.oracle.configuration.dao;

import eu.ginere.jdbc.oracle.DaoManagerException;
import eu.ginere.util.test.TestInterface;

/**
 * Intefaz de la configuraci�n de los DAO.
 * 
 */
public interface ConfigurationDAOInterface extends TestInterface{

	/**
	 * Obtiene el nombre de la propiedad.
	 * @param propertyName String El nombre de la propiedad.
	 * @return String El nombre de la propiedad.
	 * @throws DaoManagerException La excepcion lanzada.
	 */
	public String getProperty(String propertyName) throws DaoManagerException;
	
	/**
	 * Nos dice si existe la propiedad.
	 * @param propertyName String El nombre de la propiedad.
	 * @return TRUE si existe, FALSE, si no.
	 * @throws DaoManagerException La excepcion lanzada.
	 */
	public boolean exists(String propertyName) throws DaoManagerException;
	
	/**
	 * Inserta una nueva propiedad.
	 * @param propertyName String El nombre de la propiedad.
	 * @param defaultValue String El valor por defecto de la propiedad.
	 * @param description String La descripci�n de la propiedad.
	 * @throws DaoManagerException La excepcion lanzada.
	 */
	public void insertProperty(String propertyName,
							   String defaultValue,
							   String description) throws DaoManagerException;
	
	/**
	 * Borra una propiedad.
	 * @param propertyName String El nombre de la propiedad.
	 * @throws DaoManagerException La excepcion lanzada.
	 */
	public void delete(String propertyName) throws DaoManagerException;

	/**
	 * Inserta un valor a una propiedad.
	 * @param propertyName String El nombre de la propiedad.
	 * @param value String El valor a dar a la propiedad.
	 * @throws DaoManagerException La excepcion lanzada.
	 */
	public void setValue(String propertyName, String value) throws DaoManagerException;

	/**
	 * Aptualiza la descripci�n de una propiedad.
	 * @param propertyName String El nombre de la propiedad.
	 * @param descripcion String La descripci�n a dar a la propiedad.
	 * @throws DaoManagerException La excepcion lanzada.
	 */
	public void updateDescripcion(String propertyName, String descripcion) throws DaoManagerException;
}
