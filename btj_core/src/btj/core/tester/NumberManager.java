/**
 * Singleton設計
 * <ID>yyyyMMddHHmmss_<XXXXXXXXXX>
 * <ID>はコンストラクタの第2引数
 * <XXXXXXXXXX>はコンストラクタの第1引数
 */

package btj.core.tester;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import btj.core.util.DateFormat;


public class NumberManager {
	public static final SimpleDateFormat DF_YMDHMS_NO
	= new SimpleDateFormat("yyyyMMddHHmmss");

	private int number;
	private String id;
	private static NumberManager instance = new NumberManager(0, "BTJ");
	private String currentNumber;

	private NumberManager(int number, String id){
		super();
		this.number = number;
		this.id = id;
		this.currentNumber = null;
	}

	/**
	 * NumberManagerのSingleton Instanceを返す。
	 * @return
	 */
	public static NumberManager getInstance(){
		return instance;
	}

	public String next() {
		number++;
		StringBuffer sb = new StringBuffer();
		sb.append(id);
		sb.append(DF_YMDHMS_NO.format(new Date()));
		sb.append("_");
		sb.append(padding(number, 10));
		currentNumber = sb.toString();
		return currentNumber;
	}

	private String padding(int number, int size){
		StringBuffer sb = new StringBuffer();
		String buf = String.valueOf(number);
		int loop = size - buf.length();
		while(loop-- > 0){
			sb.append("0");
		}
		sb.append(buf);
		return sb.toString();
	}

	public String getCurrentNumber(){
		return currentNumber;
	}

	/**
	 * (設計注)
	 * ディレクトリ名重複防止のために作成したが使用しない。
	 * @return
	 */
	public String getNumberStr(){
		String numberStr = next();
		File f = new File(numberStr);
		if(f.exists()){
			//存在しない名前が生成できるまで繰り返す
			numberStr = getNumberStr();
		}
		return numberStr;
	}

	public String getId() {
		return id;
	}



}
