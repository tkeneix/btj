package btj.core.tester;

import java.util.List;

public class OrderCheckStatus {
	/**
	 * DataSetの途中の状態を示す。
	 */
	public static final short PROCESSING = 1;
	/**
	 * DataSetの終端の状態を示す。
	 */
	public static final short FINISHED = 2;

	private short value;
	private List closedList;

	public OrderCheckStatus(short value, List closedList){
		this.value = value;
		this.closedList = closedList;
	}

	/**
	 * OrderManagerによる確認処理の状態を返す。
	 * @return short
	 */
	public short getValue() {
		return value;
	}

	/**
	 * 約定済みチケットリストを返す。
	 * @return Ticket[]
	 */
	public Ticket[] getClosedList() {
		//Ticket[]でキャストするとClassCastExceptionが発生するので
		//一度Object[]に展開する。
		Object[] objArray = closedList.toArray();
		Ticket[] ret = new Ticket[objArray.length];
		for(int i=0; i<objArray.length; i++){
			ret[i] = (Ticket)objArray[i];
		}
		return ret;
	}
}
