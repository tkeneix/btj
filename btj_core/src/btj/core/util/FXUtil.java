package btj.core.util;

import java.util.HashMap;
import java.util.Map;

public class FXUtil {
    public static final String[] standardPairs = new String[]{"USDJPY", "CHFJPY", "EURJPY", "GBPJPY",
                                                                                            "AUDJPY", "NZDJPY", "CADJPY"};

	public static Map<String, String> getShortNameMap(){
		Map<String, String> ret = new HashMap<String, String>();
		ret.put("USDJPY", "UJ");
		ret.put("EURJPY", "EJ");
		ret.put("EURUSD", "EU");
		ret.put("AUDJPY", "AJ");
		ret.put("NZDJPY", "NJ");
		ret.put("CHFJPY", "HJ");
		ret.put("GBPJPY", "GJ");
		ret.put("CADJPY", "CJ");
		ret.put("UJ", "USDJPY");
		ret.put("EJ", "EURJPY");
		ret.put("EU", "EURUSD");
		ret.put("AJ", "AUDJPY");
		ret.put("NJ", "NZDJPY");
		ret.put("HJ", "CHFJPY");
		ret.put("GJ", "GBPJPY");
		ret.put("CJ", "CADJPY");
		return ret;
	}
}
