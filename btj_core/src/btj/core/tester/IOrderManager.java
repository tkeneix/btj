package btj.core.tester;

public interface IOrderManager {

	/**
	 * 新規注文を受理し対応するチケットを発行する。
	 * @param strategy
	 * @param type
	 * @param delta
	 * @param lots
	 * @param value
	 * @return
	 */
	public abstract Ticket requestNewOrder(String strategy, String dsName,
			short type, short delta, double lots, double value, short expired);

	/**
	 * 新規注文を受理し対応するチケットを発行する。
	 * @param strategy
	 * @param type
	 * @param delta
	 * @param value
	 * @return
	 */
	public abstract Ticket requestNewOrder(String strategy, String dsName,
			short type, short delta, double value, short expired);

	/**
	 * 返済注文を受理し対応するチケットへ関連付ける。
	 * @param ticket
	 * @param type
	 * @param value
	 * @return
	 */
	public abstract Ticket requestRepayOrder(Ticket ticket, short type,
			double value);

	/**
	 * 残っているチケットを強制全返済する
	 * @param ticket
	 * @param type
	 * @param value
	 * @return
	 */
	public abstract boolean requestRepayAllOrder(String strategy);

	public abstract Ticket requestCancel(Ticket ticket);

	public abstract OrderCheckStatus nextCheck(int num);

	public abstract void dumpHistoricalTicketTable();

	public abstract void dumpStdTicketTable();

	public abstract void shutdown();

}