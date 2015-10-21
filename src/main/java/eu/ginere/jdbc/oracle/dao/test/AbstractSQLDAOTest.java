package eu.ginere.jdbc.oracle.dao.test;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import eu.ginere.base.util.test.TestResult;
import eu.ginere.jdbc.oracle.AbstractDAO;
import eu.ginere.jdbc.oracle.DataBase;
import eu.ginere.jdbc.test.JDBCTestUtils;

public abstract class AbstractSQLDAOTest extends TestCase {
	public static final Logger log = Logger.getLogger(AbstractSQLDAOTest.class);

	protected final AbstractDAO DAO;
	protected final boolean removeBackEnd;


	protected AbstractSQLDAOTest(AbstractDAO DAO){
		this.DAO=DAO;
		this.removeBackEnd=false;
	}


	protected AbstractSQLDAOTest(AbstractDAO DAO,boolean removeBackEnd){
		this.DAO=DAO;
		this.removeBackEnd=removeBackEnd;
	}
	
	public void innerTestBackEnd() throws Exception {
		try {
			setDataSource();		

			long elementNumber=DAO.count();
			log.info("elementNumber:"+elementNumber);
			
			TestResult test=DAO.test();
			
			if (!test.isOK()){
				log.error("Test not OK:"+test);	
			}
			
		} catch (Exception e) {
			log.error("", e);
			throw e;
		}
	}

	protected String getFilePropertiesName() throws Exception {
		return "conf/jdbc.properties";
	}
	
	protected void setDataSource() throws Exception {
		String filePropertiesName=getFilePropertiesName();
		DataSource dataSource = JDBCTestUtils.createDataSourceFromPropertiesFile(filePropertiesName);
		
		DataBase.initDatasource(filePropertiesName,dataSource);
	}
	

	
}
