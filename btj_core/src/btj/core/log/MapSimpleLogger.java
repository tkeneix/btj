package btj.core.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

import btj.core.tester.FileManager;


public class MapSimpleLogger implements IMemoryLogger{
	private String prefix;
	private String dirPath;
	private FileManager fmng;
	private PrintWriter pw;
	private boolean initialized = false;

	private TreeMap<String, Object> memlog = new TreeMap(new StringComparetor());

	/**
	 * @param prefix
	 * @param dirPath 最後尾に/を付与すること
	 * @throws FileNotFoundException
	 */
	public MapSimpleLogger(String prefix, String dirPath){
		this.prefix = prefix;
		this.dirPath = dirPath;
	}

	public MapSimpleLogger(String prefix, FileManager fmng){
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

	public synchronized Object get(String key){
		return memlog.get(key);
	}

	public synchronized Object remove(String key){
		return memlog.remove(key);
	}

	public synchronized void put(String key, Object value){
		if(!initialized){
			init();
		}
		memlog.put(key, value);
	}

	public synchronized void dump(){
		if(pw != null){
			Iterator ite = memlog.values().iterator();
			if(ite.hasNext()){
				Object next = ite.next();
				if(next instanceof IHeader){
					pw.print(((IHeader)next).getHeader());
				}
				pw.print(next.toString());
				while(ite.hasNext()){
					pw.print(ite.next().toString());
				}
			}
			pw.flush();
			pw.close();
		}
	}

	static class StringComparetor implements Comparator, Serializable{

		public int compare(Object arg0, Object arg1) {
			String left = (String)arg0;
			String right = (String)arg1;
			return left.compareTo(right);
		}

	}
}
