package btj.core.dataset;

import btj.core.tester.Ticket;

public class DoubleData extends Data{
	private double value;

	public DoubleData(double value){
		this.value = value;
	}

	public Ticket judge(Ticket ticket){

		return ticket;
	}

	public double getValue() {
		return value;
	}


}
