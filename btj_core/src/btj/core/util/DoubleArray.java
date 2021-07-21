package btj.core.util;

import java.io.Serializable;

public class DoubleArray implements Serializable{
	private double[] _list;
	private static final int DEFAULT_LIST_SIZE = 50;
	private int _current;

	public DoubleArray(int size){
		_current = 0;
		_list = new double[size];
	}

	public DoubleArray(){
		this(DEFAULT_LIST_SIZE);
	}

	private void ensureCapacity(){
		if(_list.length-1 < _current){
			double[] newlist = new double[_list.length*2];
			System.arraycopy(_list, 0, newlist, 0, _current);
			_list = newlist;
		}

	}

	public void add(double d){
		ensureCapacity();
		_list[_current++] = d;
	}

	public double get(int num){
		if(num < 0) return 0.0;
		return _list[num];
	}

	public void padding(int num, double d){
		for(int j=0; j<num; j++){
			add(d);
		}
	}

	public int capacity(){
		return _list.length;
	}

	public int length(){
		return _current;
	}

	public void clear(){
		_list = null;
	}

	public void reset(){
		for(int i=0; i<_list.length; i++){
			_list[i] = 0.0;
		}
		_current = 0;
	}

	public double[] getArray(){
		return _list;
	}

	public double[] getArraySequence(){
		double[] ret = new double[_current];
		for(int i=0; i<_current; i++){
			ret[i] = i+1;
		}
		return ret;
	}

}
