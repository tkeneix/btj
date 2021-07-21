package btj.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import btj.core.util.DateFormat;

public class DailyLogger {
	public static final SimpleDateFormat DF_YMD_NO
	= new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat DF_YMDHMSS_SL
	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

	public static final short DEBUG 	= (short)0;
	public static final short INFO 	= (short)1;
	public static final short WARN 	= (short)2;
	public static final short ERR 		= (short)3;

	private short logLevel;
	private String prefix;
	private String dirPath;

	private String currentDateStr;
	private PrintWriter pw;

	/**
	 * @param prefix
	 * @param dirPath 最後尾に/を付与すること
	 * @param level
	 */
	public DailyLogger(String prefix, String dirPath, short level, int fileCount){
		this.logLevel = level;
		this.prefix = prefix;
		this.dirPath = dirPath;
		File dirObj = new File(dirPath);
		if(!dirObj.exists()){
			dirObj.mkdirs();
		}
		File[] listFiles = dirObj.listFiles();
		if(listFiles.length > fileCount){
			Arrays.sort(listFiles);
			for(int i=0; i<(listFiles.length - fileCount); i++){
				listFiles[i].delete();
			}
		}
	}

	public synchronized void write(short level, String message){
		try{
			Date current = new Date();
			if(level >= logLevel){
				String currentStr = DF_YMD_NO.format(current);
				if(currentDateStr == null || !currentDateStr.equals(currentStr)){
					currentDateStr = currentStr;
					if(pw != null){
						pw.flush();
						pw.close();
					}
					pw = new PrintWriter(new FileOutputStream(dirPath + prefix + "_" + currentDateStr + ".log", true));
				}

				StringBuilder sb = new StringBuilder();
				sb.append(DF_YMDHMSS_SL.format(current)).append(" ");
				if(level == DEBUG){
					sb.append("[D] ");
				}else if(level == INFO){
					sb.append("[I] ");
				}else if(level == WARN){
					sb.append("[W] ");
				}else if(level == ERR){
					sb.append("[E] ");
				}
				sb.append(message);
				System.out.println(sb.toString());
				pw.println(sb.toString());
				pw.flush();
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	public synchronized void close(){
		if(pw != null){
			pw.flush();
			pw.close();
		}
	}

}
