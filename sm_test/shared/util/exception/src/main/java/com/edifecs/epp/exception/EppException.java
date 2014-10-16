package com.edifecs.epp.exception;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.*;

/**
* Exception class that is used when there is an issue sending a message through
* the message API. Constructor will change the original instance of Throwable
* (if any) into an instance of MessageException before setting it as the cause.
* This is to prevent ClassNotFoundExceptions when the stack trace includes
* exception classes that are not visible to the receiver's OSGi bundle. The
* original class name can be retrieved using
* <code>getOriginalExceptionClassName()</code>.
* 
* @author mayank kumar Gururani
*/


public abstract class EppException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * An immutable List<String> containing any arguments passed to the
	 * ExceptionTemplate from which this EppException was created. Although
	 * these properties are usually inserted into the error message
	 */
	private String[] arguments = new String[0];

	/**
	 * The HTTP status code that this error represents, if it is returned as the
	 * result of a REST command. Defaults to 500.
	 */
	public int getHttpStatus() {
        return 500;
    };

    /**
     * The message to be output to the node's log file. Will be translated into
     * the system language, if a translation is available.
     */
    @Override
    abstract public String getMessage();


    /**
     *
     * @param t
     */
    public EppException(String[] arguments, Throwable t) {
        super(t);
        this.arguments = arguments;
    }

    public EppException(String[] arguments) {
        super();
        this.arguments = arguments;
    }

    /**
	 * 
	 * @param t
	 */
	public EppException(Throwable t) {
        super(t);
    }
	
	/**
	 * 
	 */
	public EppException() {
		super();
	}

	
	/**
	 * 
	 * @param t
	 * @return
	 * @throws IOException
	 */
	private byte[] serializeException(Throwable t) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(t);
			return bos.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
			bos.close();
		}
	}

	/**
	 * 
	 * @param currentLocale
	 * @return
	 * @throws IOException
	 */
	private static ResourceBundle getResourceBundle(Locale currentLocale) throws IOException {
		   FileInputStream fis = null;
		   InputStream inp = null;
			try {
				inp = EppException.class.getResourceAsStream("/exception-messages/"+currentLocale.getLanguage()+".properties");
				return new PropertyResourceBundle(inp);
			} catch(IOException e){
				 try{
					 inp = ClassLoader.getSystemClassLoader().getResourceAsStream("/exception-messages/"+currentLocale.getLanguage()+".properties");
					 return new PropertyResourceBundle(inp);
				 } catch(IOException e2){
					 fis = new FileInputStream("/exception-messages/"+currentLocale.getLanguage()+".properties");
						return new PropertyResourceBundle(fis);
				}
			} finally {
			  if (fis != null){
				  fis.close();
			  }
			  
			  if (inp != null){
				  inp.close();
			  }
			}
	   }
	
	@Override
	public String getLocalizedMessage() {
		Locale currentLocale =  Locale.getDefault();
		Object[] messageArguments = new Object[arguments.length];
		//en.properties
		ResourceBundle messages = null;
		try {
			messages = getResourceBundle(currentLocale);
		} catch (IOException e) {
			try {
				messages = getResourceBundle(new Locale("en"));
			} catch (IOException e1) {
				// Do Nothing
			}
		}
		int incCounter = 0;
		for (String value : arguments){
			messageArguments[incCounter] = value;
			incCounter ++;
		}
        String msg;
        try {
            msg = messages.getString(getClass().getName());
        } catch (MissingResourceException e) {
            msg = getMessage();
        }
		MessageFormat formatter = new MessageFormat(msg);
		formatter.setLocale(currentLocale);
		return formatter.format(messageArguments);
		}

    /**
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Exception deserializeException(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			return (Exception) in.readObject();
		} finally {
			if (in != null) {
				in.close();
			}
			bis.close();
		}
	}
}
