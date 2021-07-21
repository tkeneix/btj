package btj.core.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import btj.core.tester.FileManager;
import btj.core.util.DateFormat;


public class CSVSimpleLogger implements ISimpleLogger, Serializable {
	public static final SimpleDateFormat DF_YMD_NO
	= new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat DF_YMDHMSS_SL
	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

	private String prefix;
	private String dirPath;
	private FileManager fmng;
	private PrintWriter pw;
	private boolean initialized = false;

	/**
	 * @param prefix
	 * @param dirPath 最後尾に/を付与すること
	 * @throws FileNotFoundException
	 */
	public CSVSimpleLogger(String prefix, String dirPath){
		this.prefix = prefix;
		this.dirPath = dirPath;
	}

	public CSVSimpleLogger(String prefix, FileManager fmng){
		this.prefix = prefix;
		this.fmng = fmng;
	}

	private synchronized void init(){
		if(fmng == null){
			File dirObj = new File(dirPath);
			if(!dirObj.exists()){
				dirObj.mkdirs();
			}
			try{
				pw = new PrintWriter(new FileOutputStream(dirPath + prefix + ".csv", true));
				initialized = true;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else{
			try {
				pw = new PrintWriter(fmng.getSubDirStream(prefix + ".csv", true));
				initialized = true;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	/*
	 * @see log.ISimpleLogger#write(java.lang.String)
	 */
	@Override
	public synchronized void write(String message){
		if(!initialized){
			init();
		}
		pw.println(message);
        pw.flush();
	}

	/*
	 * @see log.ISimpleLogger#close()
	 */
	@Override
	public synchronized void close(){
		if(pw != null){
			pw.flush();
			pw.close();
		}
	}


}
