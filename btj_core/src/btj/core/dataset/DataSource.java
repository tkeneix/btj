package btj.core.dataset;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import btj.core.util.DateFormat;
import btj.core.util.Parameters;


public class DataSource {
	private static final String PLATFORM_DEFINITION_FILENAME = "btj.platform";
	private String propFileName;
	private Parameters params;
	private Parameters platform;

	public DataSource(String propFileName) throws IOException{
		this(new Parameters(propFileName));
		this.propFileName = propFileName;
	}

	public DataSource(Parameters params) throws IOException{
		this.params = params;
		this.platform = new Parameters(PLATFORM_DEFINITION_FILENAME);
	}

	public DataSet getDataSet(){
		DataSet ret = null;
		try{
			//パラメタ取得
			//例外処理は省略
			String factoryClassName = params.getString("dataSetFactoryClass", "");
			String argsClassName = params.getString("dataSetFactoryArgsClass", "");
			String judgementClassName = params.getString("judgementClass", "");
			boolean enableOutputDataSet = params.getBoolean("enableOutputDataSet", "false");
			String outputFileName = params.getString("outputFileName", "");
			String outputFileKind = params.getString("outputFileKind", "");
			boolean enablePrintLoadTime = params.getBoolean("enablePrintLoadTime", "true");
			boolean enablePrintParameters = params.getBoolean("enablePrintParameters", "true");

			//FactoryArgsの初期化
			Class argsClass = Class.forName(argsClassName);
			DataSetFactoryArgs argsImpl = (DataSetFactoryArgs)argsClass.newInstance();
			Class[] scArray = getSuperClass(argsClass);

			if(enablePrintParameters){
				System.out.println("--- DataSource Parameters ---");
			}

			for(int j=0; j<scArray.length; j++){
				Field[] fArray = scArray[j].getDeclaredFields();
				for(int i=0; i<fArray.length; i++){
					//System.out.println(fArray[i].getType().getName());

					//フィールド名に対応するプロパティを取得
					String value = params.getString(fArray[i].getName(), "");
					String printStr = value;
					fArray[i].setAccessible(true);
					if(fArray[i].getType() == Integer.TYPE){
						if(!(value == null || value.equals(""))){
							fArray[i].setInt(argsImpl, Integer.parseInt(value));
						}

					}else if(fArray[i].getType() == Boolean.TYPE){
						if(!(value == null || value.equals(""))){
							fArray[i].setBoolean(argsImpl, Boolean.parseBoolean(value));
						}
					}else if(fArray[i].getType().isArray()){
						if(fArray[i].getName().equals("mapTargetList")
								&& !(value == null || value.equals(""))){
							String[] strArray = value.split(",");
							MapDataTarget[] targetList = new MapDataTarget[strArray.length];
							for(int n=0; n<targetList.length; n++){
								targetList[n] = new MapDataTarget(strArray[n]);
							}
							fArray[i].set(argsImpl, targetList);
						}else{
							//メンバ変数が配列の場合はskip
							printStr = "NA";
						}
					}else if(fArray[i].getType().getName().equals("java.util.Date")){
						//Dateクラスの場合はDateインスタンスを生成する
						Date dateBuf = DateFormat.DF_YMDHMS_SL.parse(value);
						fArray[i].set(argsImpl, dateBuf);
					}else if(fArray[i].getType().getName().equals("btj.core.dataset.Judgement")){
						if(!judgementClassName.equals("") && judgementClassName != null){
							Object objBuf = Class.forName(judgementClassName).newInstance();
							fArray[i].set(argsImpl, objBuf);
							printStr = objBuf.toString();
						}
					}else if(fArray[i].getType().getName().equals("java.lang.String")){
						fArray[i].set(argsImpl, convertPlatform(value));
					}else{
						fArray[i].set(argsImpl, value);
					}

					if(enablePrintParameters){
						System.out.println(fArray[i].getName() + "=" + printStr);
					}
				}
			}
			if(enablePrintParameters){
				System.out.println("-----------------------------");
			}

			long dsLoadStartTime = 0L;
			long dsLoadEndTime = 0L;

			if(enablePrintLoadTime) dsLoadStartTime = System.currentTimeMillis();

			//Factoryの初期化
			Class factoryClass = Class.forName(factoryClassName);
			DataSetFactory factoryImpl = (DataSetFactory)factoryClass.newInstance();
			ret = factoryImpl.create(argsImpl);

			if(enablePrintLoadTime){
				dsLoadEndTime = System.currentTimeMillis();
				System.err.println("dsLoadTime=" + (dsLoadEndTime - dsLoadStartTime) + "msec");
			}

			//ファイル出力
			if(enableOutputDataSet){
				PrintWriter pw = new PrintWriter(new FileWriter(outputFileName));
				while(ret.hasNext()){
					if(outputFileKind.equalsIgnoreCase("CSV")){
						pw.println(((Data)ret.next()).toCSV());
					}else{
						pw.println((ret.next()).toString());
					}
				}
				pw.flush();
				pw.close();
				ret.clearOffset();
			}

			return ret;
		}catch(Exception ex){
			ex.printStackTrace();
			throw new RuntimeException("Error: check file of properties.");
		}
	}

	public String convertPlatform(String value){
		String defaultPath = platform.getString("dataSourceDefaultPath", "");
		if(value.indexOf("{DataSource}") != -1){
			return value.replaceAll("\\{DataSource\\}", defaultPath);
		}else{
			return value;
		}
	}

	public Class[] getSuperClass(Class cls){
		ArrayList list = new ArrayList();
		list.add(cls);
		Class parent = cls.getSuperclass();
		while(parent != null){
			if(parent.getName().equals("java.lang.Object")) break;
			list.add(parent);
			parent = parent.getSuperclass();
		}

		Object[] objArray = list.toArray();
		Class[] clsArray = new Class[objArray.length];
		System.arraycopy(objArray, 0, clsArray, 0, objArray.length);
		return clsArray;
	}

	public String getPropFileName() {
		return propFileName;
	}

	public Parameters getParameters() {
		return params;
	}

}
