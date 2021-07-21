/**
 * 最大サイズを指定して領域を再利用するArrayクラス。
 * 最大サイズを超えるとラップアラウンドして先頭から値を上書きする。
 */

package btj.core.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RollingArray implements Serializable{
	int capacity;
	double[] list;
	/**
	 * 内部配列の添字。次にaddする添字を表す。初期値は0。
	 */
	int offset;
	boolean lap;

	public RollingArray(int capacity){
		this.capacity = capacity;
		this.list = new double[capacity];
		this.offset = 0;
		this.lap = false;
	}

	public int getOffset(int num){
		int ret = 0;
		/**
		 * ケース１：
		 * offset=3, num=0の場合
		 * offset=2が現在の添字。num=0の場合は、2を返す。
		 * ケース２：
		 * offset=3, num=1の場合→1を返す。
		 * offset=3, num=2の場合→0を返す。
		 * offset=3, num=3の場合→capacity-1を返す。
		 */
		if(num >= capacity){
			throw new RuntimeException("capacityは" + capacity + "です。 num=" + num);
		}else if(num < offset){
			//numが現在のoffsetより小さい場合
			ret = offset -1 - num;
		}else{
			//numが現在のoffset以上の場合
			ret = capacity -1 - (num - offset);
		}

		return ret;
	}

	public double get(int num){
		double ret = 0.0;
		if(num < 0){
			throw new RuntimeException(num + "はサポートされていません。");
		}else{
			ret = list[getOffset(num)];
		}
		return ret;
	}

	public void add(double value){
		if(offset >= capacity){
			lap = true;
			offset = 0;
		}
		list[offset] = value;
		offset++;
	}

	public void plusAndAdd(double value){
		double buf= get(getOffset(0)) + value;
		add(buf);
	}

	public void minusAndAdd(double value){
		double buf= get(getOffset(0)) - value;
		add(buf);
	}

	public int capacity(){
		return capacity;
	}

	public int length(){
		return capacity;
	}

	public boolean isLap(){
		return lap;
	}

	public void clear(){
		list = null;
	}

	public void reset(){
		for(int i=0; i<capacity; i++){
			list[i] = 0.0;
		}
		offset = 0;
		lap = false;
	}

	public double[] getArray(){
		return getArray(capacity);
	}

	public double[] getArray(int size){
		double[] ret = new double[size];
		for(int i=0; i<size; i++){
			ret[i] = list[getOffset(size - 1 - i)];
		}
		return ret;
	}

	public double[] getCumulativeArray(){
		return getCumulativeArray(capacity);
	}

	public double[] getCumulativeArray(int size){
		double[] ret = new double[size];
		ret[0] = list[getOffset(size-1)];
		for(int i=1; i<size; i++){
			ret[i] = ret[i-1] + list[getOffset(size - 1 - i)];
		}
		return ret;
	}

	public double[] getRawArray(){
		return list;
	}

	public double[] getArraySequence(){
		return getArraySequence(capacity);
	}

	public double[] getArraySequence(int size){
		double[] ret = new double[size];
		for(int i=0; i<size; i++){
			ret[i] = i+1;
		}
		return ret;
	}

	public double ave(){
		return ave(capacity);
	}

	public double ave(int size){
		double ret = sum(size);
		return (ret/(double)size);
	}

	public double sum(){
		return sum(capacity);
	}

	public double sum(int size){
		double ret = 0.0;
		for(int i=0; i<size; i++){
			ret += get(i);
		}
		return ret;
	}

	public double var(){
		return var(capacity);
	}

	public double var(int size){
		double ret = 0.0;
		double avg = ave(size);
		for(int i=0; i<size; i++){
			ret += (get(i) - avg) * (get(i) - avg);
		}
		return ret/(double)(size - 1);
	}

	public double stdev(){
		return stdev(capacity);
	}

	public double stdev(int size){
		double ret = 0.0;
		return Math.sqrt(var(size));
	}

	public double correl(){
		return correl(capacity);
	}

	public double correl(int size){
		double[] sequence = getArraySequence(size);
		double[] arrays = getCumulativeArray(size);

		return MathUtil.correl(sequence, arrays, size);
	}

	public double plus(int size){
		double ret = 0.0;
		for(int i=0; i<size; i++){
			if(get(i) > 0.0){
				ret++;
			}
		}
		return ret;
	}

	public double minus(int size){
		double ret = 0.0;
		for(int i=0; i<size; i++){
			if(get(i) <= 0.0){
				ret++;
			}
		}
		return ret;
	}

	public String toString(){
	    double[] bufArray = getArray();
	    List<String> bufList = new ArrayList<String>();
	    for(int i=0; i<bufArray.length; i++){
	        bufList.add(String.valueOf(bufArray[i]));
	    }
	    return StrUtil.listToCSV(bufList);
	}

	public static void main(String[] args){
		RollingArray ra = new RollingArray(5);
		for(int i=0; i<5; i++){
			ra.add(i);
		}
//		System.out.println("0=" + ra.get(0));
//		System.out.println("1=" + ra.get(1));
//		System.out.println("4=" + ra.get(4));
//
//		System.out.println("sum=" + ra.sum());
//
//		ra.add(5);
//		ra.add(6);
//
		System.out.println("0=" + ra.get(0));
		System.out.println("1=" + ra.get(1));
//		System.out.println("4=" + ra.get(4));

		double[] getarray = ra.getArray();
		for(int i=0; i<getarray.length; i++){
			System.out.print(getarray[i] + " ");
		}

//		System.out.println("sum=" + ra.sum());

	}
}
