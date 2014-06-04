package eu.ginere.util.test;

import java.util.Vector;

import eu.ginere.util.exception.ExceptionUtils;

/**
 * Clase para los test.
 * 
 * @author ventura
 *
 */
final public class TestResult {

	private final String nombre;
	private boolean isError=false;
	private String errorMessage;
	private Vector<TestResult> childs=new Vector<TestResult>();
	private Throwable exception=null;;
	
	public TestResult(String nombreDelSistema){
		this.nombre=nombreDelSistema;
	}
	
	public TestResult(Class<?> clazz){
		this.nombre=clazz.getName();
	}

	public void addError(String string) {
		this.errorMessage=string;
		this.exception=null;
		this.isError=true;		
	}

	public void addError(String string,Throwable e) {
		this.errorMessage=string;
		this.exception=e;
		this.isError=true;		
	}
	public void add(TestResult test) {
		this.isError=test.isError;
		this.childs.add(test);
	}
	
	public String toString(){
		StringBuilder buffer=new StringBuilder();
		toString(buffer,"");
		return buffer.toString();
	}
	
	private void toString(StringBuilder buffer,String level){			
		buffer.append(level);
		buffer.append(nombre);
		buffer.append(": ");
		if (isError) {
			buffer.append("ERROR");
		} else {
			buffer.append("OK");
		}
		buffer.append('\n');
		
		if (errorMessage!=null || exception!=null){
			buffer.append(level);
			buffer.append(errorMessage);
			buffer.append(ExceptionUtils.formatException(exception));
			buffer.append('\n');			
		}
		
		for (TestResult test:childs){
			test.toString(buffer,level+'\t');
		}
	}

	public boolean isOK() {
		return !isError;
	}
}
