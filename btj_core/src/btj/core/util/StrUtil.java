package btj.core.util;

import java.util.List;

public class StrUtil {

	/**
	 * 全角数字を半角に変換します。
	 * @param s 変換元文字列
	 * @return 変換後文字列
	 */
	public static int zenkakunumToHankakunum(String s) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c >= '０' && c <= '９') {
				sb.setCharAt(i, (char)(c - '０' + '0'));
			}
		}
		return Integer.parseInt(sb.toString());
	}

	public static String paddingZeroFront(int number, int size){
		StringBuffer sb = new StringBuffer();
		String buf = String.valueOf(number);
		int loop = size - buf.length();
		while(loop-- > 0){
			sb.append("0");
		}
		sb.append(buf);
		return sb.toString();
	}

	public static String getText(String allText, String startText, boolean isWithStartText,
	                                        String endText, boolean isWithEndText){
	    int S = allText.indexOf(startText);
	    if(S != -1){
	        //開始文字列が見つかった場合
	        if(!isWithStartText){
	            S =  S + startText.length();
	        }
	    }else{
	        //開始文字列が見つからなかった場合
	        S = 0;
	    }

	    int E = allText.lastIndexOf(endText);
	    if(E != -1){
	        //終端文字列が見つかった場合
	        if(isWithEndText){
	            E = E + endText.length();
	        }
	    }else{
	        //終端文字列が見つからなかった場合
	        E = allText.length();
	    }

	    return allText.substring(S, E);
	}

	public static String arrayToCSV(Object[] target){
	    StringBuilder sb = new StringBuilder();
	    for(int i=0; i<target.length; i++){
	        String targetStr = target[i].toString();
	        boolean isカンマ = false;
	        if(targetStr.indexOf(",") != -1){
	            //カンマを含む場合は"で括る
	            isカンマ = true;
	        }
	        if(isカンマ)sb.append("\"");
	        sb.append(targetStr);
	        if(isカンマ)sb.append("\"");
	        if(i != target.length - 1){
	            sb.append(",");
	        }
	    }
	    return sb.toString();
	}

	   public static String listToCSV(List<String> target){
	        StringBuilder sb = new StringBuilder();
	        for(int i=0; i<target.size(); i++){
	            String targetStr = target.get(i);
	            boolean isカンマ = false;
	            if(targetStr.indexOf(",") != -1){
	                //カンマを含む場合は"で括る
	                isカンマ = true;
	            }
	            if(isカンマ)sb.append("\"");
	            sb.append(targetStr);
	            if(isカンマ)sb.append("\"");
	            if(i != target.size() - 1){
	                sb.append(",");
	            }
	        }
	        return sb.toString();
	    }

	public static double parseDouble(String target){
	    String buf = target.replaceAll(",", "");
	    return Double.parseDouble(buf);
	}

	public static void main(String[] args){
	    String test = "AAABBBCCC111222333";
	    System.out.println(getText(test, "BBB", true, "222", true));
	    System.out.println(getText(test, "BBB", true, "222", false));
	    System.out.println(getText(test, "BBB", false, "222", true));
	    System.out.println(getText(test, "BBB", false , "222", false));
	}

}
