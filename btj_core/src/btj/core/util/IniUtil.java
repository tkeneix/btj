/*
 * ini形式のファイルをロードする
 *
 * [セクション]
 * key=value
 *
 * 注意点
 * ・ファイル更新時にはreloadが必要
 * ・keyとvalueの前後空白は除去する
 * ・行頭の#はコメントアウトとみなす
 */

package btj.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IniUtil {
	private File fPath;
	private Map propTable;


	public IniUtil(String path) throws IOException{
		this.fPath = new File(path);
		this.propTable = new HashMap<String, Properties>();
		_load();
	}

	private void _load() throws IOException{
		String strLine, value  = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fPath));
		boolean isInSection = false;
		try{
			String currentSec = "";
			while((strLine = bufferedReader.readLine()) != null){
				//コメント行のチェック
				if(strLine.length() >= 1 && strLine.charAt(0) == '#'){
					continue;
				}

				//セクションの解析
				int secStart = strLine.indexOf("[");
				if(secStart != -1){
					//セクションがある場合、セクション名を取得
					int secEnd = strLine.lastIndexOf("]");
					if(secEnd == -1){
						throw new RuntimeException("不正なini形式(セクション)です。strLine=" + strLine);
					}
					currentSec = strLine.substring(secStart, secEnd);
					Properties secProp = new Properties();

					propTable.put(currentSec, secProp);
				}else{
					//セクションでない場合は、key=valueの解析を行う
					if(currentSec == ""){
						//一度もセクションが出現していない場合は読み飛ばす
					}else{
						//key=valueの解析
						String[] keyValueList = strLine.split("=");
						if(keyValueList.length != 2){
							throw new RuntimeException("不正なini形式(key=value)です。strLine=" + strLine);
						}

						Properties cProp = (Properties)propTable.get(currentSec);
						cProp.setProperty(keyValueList[0].trim(), keyValueList[1].trim());
					}
				}
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			bufferedReader.close();
		}
	}

	public void reload() throws IOException{
		this.propTable = new HashMap<String, Properties>();
		_load();
	}

	public String getString(String section, String key, String defaultValue){
		String ret = defaultValue;
		Properties cProp = getSection(section);
		if(cProp != null){
			String value = cProp.getProperty(key);
			if(value != null){
				ret = value;
			}
		}

		return ret;
	}

	public Properties getSection(String section){
		Properties cProp = (Properties)propTable.get(section);
		return cProp;
	}

}
