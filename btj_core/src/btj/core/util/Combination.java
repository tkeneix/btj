package btj.core.util;

import java.util.*;

public class Combination{
	int[] c;
	int [] list;
	int n, r, count;
	ArrayList _combiList;	//int配列のリスト構造

	public Combination(int n, int r){
		this.n = n;
		this.r = r;
		c = new int[r+1];
		list = new int[r];
		_combiList = new ArrayList();
		count = 0;
		for ( int i=0; i<r+1; i++ )
		c[i] = 0;
		combine(1);
	}
	public void combine( int m ) {
		if ( m <= r ) {
			for ( int i=c[m-1]+1; i<=n-r+m; i++ ){
				c[m] = i;
				combine(m+1);
			}
		}
		else {
			count = count + 1 ;
			for ( int i=1; i<r+1; i++ ){
				list[i-1] = c[i];
				//System.out.print(" " + list[i-1]);
			}
			//System.out.println();
			_combiList.add(list);
			list = new int[r];
		}
	}

	public ArrayList getCombiList(){
		return _combiList;
	}
}
