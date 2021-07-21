package btj.core.dataset;

import btj.core.tester.Ticket;

public abstract class Judgement {
	private DataSetManager dsMng;

	public DataSetManager getDsMng() {
		return dsMng;
	}
	public void setDsMng(DataSetManager dsMng) {
		this.dsMng = dsMng;
	}

	public abstract Ticket judge(Data data, Ticket ticket);
	public abstract void checkNewOrder(Data data, Ticket ticket);
	public abstract void checkRepayOrder(Data data, Ticket ticket);
}
