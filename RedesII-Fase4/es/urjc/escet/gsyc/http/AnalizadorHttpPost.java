package es.urjc.escet.gsyc.http;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Esta clase analiza la petición GET, proporcionándonos el PATH que nos piden y las variables que lo acompañan
 * @author LuisGP
 *
 */
public class AnalizadorHttpPost {
	/** TODO
	 *  Comprobar peticion GET o POST en GestorDePeticiones
	 */
	
	private static final String fileExtractorRegEx = "POST (/\\S*)\\s+HTTP/1\\.[01]";
	private static final Pattern fileExtractorPattern = Pattern.compile(fileExtractorRegEx);
	
	private static final String varExtractorRegEx = "([^=&]+)=([^=&]+)";
	private static final Pattern varExtractorPattern = Pattern.compile(varExtractorRegEx);

	private static final String URL_ENCODING = "ISO-8859-1";
	
	private static URL getURL(String firstLine) throws MalformedURLException{
		Matcher m = fileExtractorPattern.matcher(firstLine);
		if(!m.matches())
			return null;
		return new URL ("http://dummy" + m.group(1));
	}
	
	private static String getBody(BufferedReader in) throws MalformedURLException{
		String body = new String();
		
		//	Leemos resto de cabeceras, por si nos interesan.
		try{
			String line = null;
			
			//byte[] msg = new byte[size];
			int size = 8*1024;
			String cl = "Content-Length: ";
			
			while( (line=in.readLine()) != null){
				//Si la línea está en blanco, entonces se han terminado las cabeceras
				System.out.println(line);
				if(line.contains(cl)){
					String aux = line.substring(cl.length());
					//System.out.println(aux);
					size = Integer.parseInt(aux);
				}
				if(line.contentEquals(""))
					break;
			}
			
			//int c;
			char[] cuerpo = new char[size];
			
			in.read(cuerpo);
			body = new String(cuerpo);
			
			body = URLDecoder.decode(body,"ISO-8859-1");
			
			//System.out.println(body);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return body;
	}

	public static Map<String, String> getVariables(BufferedReader in) throws MalformedURLException{
		String body = getBody(in);
		
		System.out.println("Cuerpo:\n"+body);
		
		Matcher m = varExtractorPattern.matcher(body);

		Map<String, String> result = null;
		while(m.find()){
			if(m.groupCount() != 2)
				return null;
			try{
				String varName = URLDecoder.decode(m.group(1), URL_ENCODING);
				String varValue = URLDecoder.decode(m.group(2), URL_ENCODING);
				if(result == null)
					result = new HashMap<String, String>();
				result.put(varName, varValue);
			} catch(UnsupportedEncodingException e){
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return result;
	}
	
	public static Map<String, String> getVars(String firstLine){
		String query = getQuery(firstLine);
		if(query == null)
			return null;
		Matcher m = varExtractorPattern.matcher(query);

		Map<String, String> result = null;
		while(m.find()){
			if(m.groupCount() != 2)
				return null;
			try{
				String varName = URLDecoder.decode(m.group(1), URL_ENCODING);
				String varValue = URLDecoder.decode(m.group(2), URL_ENCODING);
				if(result == null)
					result = new HashMap<String, String>();
				result.put(varName, varValue);
			} catch(UnsupportedEncodingException e){
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return result;
	}
	
	public static String getPath(String firstLine){
		try{
			URL url = getURL(firstLine);
			return url.getPath();
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	public static String getQuery(String firstLine){
		try{
			URL url = getURL(firstLine);
			return url.getQuery();
		} catch (MalformedURLException e){
			return null;
		}
	}
	
	public static String getRef(String firstLine){
		try{
			URL url = getURL(firstLine);
			return url.getRef();
		} catch (MalformedURLException e){
			return null;
		}	
	}
}
