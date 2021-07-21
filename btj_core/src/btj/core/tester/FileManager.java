package btj.core.tester;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileManager implements Serializable{
	/**
	 * key=ファイル名, value=ファイルのPrintWriter
	 */
	private Map printWriterTable;
	/**
	 * ログディレクトリのルートディレクトリ名称(BTJ_LOG)
	 */
	private String rootDir;
	/**
	 * ログディレクトリのサブディレクトリ名称(BTJyyyyMMddHHmmss_<XXXXXXXXXX>)
	 */
	private String subDir;

	private String totalDir;

	public FileManager(String rootDir, String subDir){
		this.printWriterTable = new HashMap();
		this.rootDir = rootDir;
		this.subDir = subDir;
		init();
	}

	private void init(){
		try{
			totalDir = rootDir;
			File root = new File(totalDir);
			if(!root.exists()){
				root.mkdir();
			}

			totalDir = totalDir
						+ File.separator
						+ subDir;
			File sub = new File(totalDir);
			if(!sub.exists()){
				sub.mkdir();
			}
		}catch(Exception ex){
			System.err.println("次の例外が発生したためログは出力されません。");
			ex.printStackTrace();
		}
	}

	public PrintWriter getSubDir(String name) throws IOException{
		return getSubDir(name, false);
	}

	public PrintWriter getSubDir(String name, boolean append) throws IOException{
		PrintWriter pw = (PrintWriter)printWriterTable.get(name);
		if(pw == null){
			//pw = new PrintWriter(new FileWriter(totalDir + File.separator + name, append));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(totalDir + File.separator + name, append), "shift-jis"));
			printWriterTable.put(name, pw);
		}
		return pw;
	}

	/**
	 * 本メソッドで生成するストリームはテーブルに登録せず、管理対象外とする。
	 * 本メソッドの呼び出し元でクローズする必要がある。
	 * @param name
	 * @param append
	 * @return
	 * @throws IOException
	 */
	public OutputStream getSubDirStream(String name, boolean append) throws IOException{
		OutputStream os = new FileOutputStream(totalDir + File.separator + name, append);
		return os;
	}


	public PrintWriter getRootDir(String name, boolean append) throws IOException{
		PrintWriter pw = (PrintWriter)printWriterTable.get(name);
		if(pw == null){
			//pw = new PrintWriter(new FileWriter(rootDir + File.separator + name, append));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(rootDir + File.separator + name, append), "shift-jis"));
			printWriterTable.put(name, pw);
		}
		return pw;
	}

	public PrintWriter getRootDir(String name) throws IOException{
		return getRootDir(name, false);
	}

	public PrintWriter remove(String name){
		PrintWriter pw = (PrintWriter)printWriterTable.remove(name);
		if(pw != null){
			pw.close();
		}
		return pw;
	}

	private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		init();
	}

	public boolean isContained(String name){
		return printWriterTable.get(name) != null ? true : false;
	}

	public void shutdown(){
		Map cloneMap = (Map)((HashMap)printWriterTable).clone();
		Iterator ite = cloneMap.keySet().iterator();
		while(ite.hasNext()){
			String name = (String)ite.next();
			PrintWriter pw = (PrintWriter)printWriterTable.remove(name);
			if(pw != null){
				pw.flush();
				pw.close();
			}
		}
	}
}
