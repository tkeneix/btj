package btj.core.log;

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


public class DailySimpleLogger implements ISimpleLogger {
	public static final SimpleDateFormat DF_YMD_NO
	= new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat DF_YMDHMSS_SL
	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

	private String prefix;
	private String dirPath;

	private String currentDateStr;
	private PrintWriter pw;

	/**
	 * @param prefix
	 * @param dirPath 最後尾に/を付与すること
	 * @param level
	 */
	public DailySimpleLogger(String prefix, String dirPath, int fileCount){
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

	/*
	 * @see log.ISimpleLogger#write(java.lang.String)
	 */
	@Override
	public synchronized void write(String message){
		try{
			Date current = new Date();
			String currentStr = DF_YMD_NO.format(current);
			if(currentDateStr == null || !currentDateStr.equals(currentStr)){
				currentDateStr = currentStr;
				if(pw != null){
					pw.flush();
					pw.close();
				}
				pw = new PrintWriter(new FileOutputStream(dirPath + prefix + "_" + currentDateStr + ".csv", true));
			}

			StringBuilder sb = new StringBuilder();
			sb.append(DF_YMDHMSS_SL.format(current)).append(",");
			sb.append(message);
			//System.out.println(sb.toString());
			pw.println(sb.toString());
			pw.flush();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
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

	public void finalyze(){
	    close();
	}

	public String getPrefix() {
		return prefix;
	}

	public String getDirPath() {
		return dirPath;
	}



}
