package eu.ginere.util.exception;

/**
 * 
 * @version $Id: CompositeFormat.java 1436768 2013-01-22 07:07:42Z ggregory $
 */
public class ExceptionUtils {

	/**
	 * Formatea una excepcion y las excepciones padre.
	 * 
	 * @param exception
	 *            excepcion
	 * @return el fomato requerido
	 */
	public static String formatException(Throwable exception) {
		StringBuilder buffer = new StringBuilder();

		formatException(exception, buffer);

		return buffer.toString();
	}


	/**
	 * formatela la excepcion.
	 * 
	 * @param exception excepcion
	 * @param buffer bufer
	 * @return StringBuilder
	 */
	public static StringBuilder formatException(Throwable exception,
												StringBuilder buffer) {

//			ByteArrayOutputStream escribe = new ByteArrayOutputStream();
//			PrintStream print = new PrintStream(escribe);
//			exception.printStackTrace(print);
//			String prpr= escribe.toString();
//	
		
		StringBuilder ret;
		
		if (exception == null) {
			ret=buffer;
			
		} else {
			buffer.append(exception.getClass().getName());
			buffer.append(": \"");
			buffer.append(exception.getMessage());
			buffer.append("\" \n");
	
			StackTraceElement array[] = exception.getStackTrace();
	
			for (StackTraceElement element : array) {
				buffer.append("\tat ");
				printStackTraceElement(element,buffer);
				buffer.append('\n');
			}
	
			if (exception.getCause() != null) {
				buffer.append("Causado por: ");
				ret=formatException(exception.getCause(), buffer);
			} else {
				ret=buffer;
			}
		}
		return ret;
	}
	
	/**
	 * IMprime un stacktrace element.
	 * @param element el elemento a pintar
	 * @param buffer el bufer donde pintar
	 */
	public static void printStackTraceElement(StackTraceElement element,StringBuilder buffer){		
		buffer.append(element.getClassName());
		buffer.append('.');
		buffer.append(element.getMethodName());
		buffer.append('(');
		buffer.append(element.getFileName());
		if (element.getLineNumber() > 0) {
			buffer.append(':');
			buffer.append(element.getLineNumber());
		}
		buffer.append(')');
	}
}
