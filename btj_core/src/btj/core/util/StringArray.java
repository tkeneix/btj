package btj.core.util;

import java.util.ArrayList;

public class StringArray{
	private ArrayList _list;

	public StringArray(){
		_list = new ArrayList();
	}

	public void add(String d){
		_list.add(new String(d));
	}

	public void put(int num, String d){
		int listsize = size()-1;
		if(listsize < num){
			for(int j=1; j<num-listsize; j++){
				_list.add(new String(""));
			}
		}
		_list.add(new String(d));
	}

	public String get(int num){
		return (String)_list.get(num);
	}

	public int size(){
		return _list.size();
	}
}
