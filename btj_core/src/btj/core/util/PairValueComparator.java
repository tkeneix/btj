package btj.core.util;

import java.util.Comparator;

public class PairValueComparator implements Comparator<PairValue> {

	public int compare(PairValue left, PairValue right) {

		int ret = 0;
		if(left.getValue() >= right.getValue()){
			//Equal=0をリターンするケースは作りこまない
			//これにより必ず配列がリターンされる（重複すると１つしかリターンされなくなる）
			ret = -1;
		}else if(left.getValue() < right.getValue()){
			ret = 1;
		}
		return ret;
	}

}
