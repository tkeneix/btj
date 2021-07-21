package btj.core.tally;

import java.io.IOException;
import java.io.Serializable;

import btj.core.tester.FileManager;
import btj.core.tester.Ticket;


public interface Tally extends Serializable, Cloneable{
	public void result(Ticket[] ticketList);
	public Tally clone();
	public void dump(FileManager fileMng) throws IOException;
	public void setStrategyName(String name);
}
