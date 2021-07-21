package btj.core.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Properties;

import btj.core.tester.FileManager;


public class Parameters implements Serializable{
	private Properties props;
	private boolean isXMLloading;
	private String prefix;			//後で変更できる
	private String fileName;		//指定されたファイル名をおぼえておく

	public Parameters(String fileName, boolean isXMLloading) throws IOException{
		this.isXMLloading = isXMLloading;
		int index = fileName.lastIndexOf(java.io.File.pathSeparator);
		if(index == -1){
			prefix = fileName;
			this.fileName = prefix;
		}else{
			prefix = fileName.substring(index + 1);
			this.fileName = prefix;
		}

		props = new Properties();

		if(isXMLloading){
		    InputStream is = new BufferedInputStream(new FileInputStream(fileName));
			props.loadFromXML(is);
			is.close();
		}else{
		    InputStreamReader is = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
			props.load(is);
			is.close();
		}
	}

	public Parameters(String fileName) throws IOException{
		this(fileName, false);
	}

	public Parameters(Properties props){
		this.props = props;
	}

	public Properties getProperties(){
		return props;
	}

	public String getString(String key, String def){
		return props.getProperty(key, def);
	}

	public short getShort(String key, String def){
		return Short.parseShort(props.getProperty(key, def));
	}

	public int getInt(String key, String def){
		return Integer.parseInt(props.getProperty(key, def));
	}

	public long getLong(String key, String def){
		return Long.parseLong(props.getProperty(key, def));
	}

	public double getDouble(String key, String def){
		return Double.parseDouble(props.getProperty(key, def));
	}

	public boolean getBoolean(String key, String def){
		return Boolean.parseBoolean(props.getProperty(key, def));
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void dump(FileManager fileMng) throws IOException{
		OutputStream os = fileMng.getSubDirStream(prefix, true);
		props.storeToXML(os, "For the backup of " + fileName);
		os.close();
	}
}
