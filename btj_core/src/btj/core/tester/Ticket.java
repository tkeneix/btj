/**
 * チケットはStrategyごとに裁番する。
 */

package btj.core.tester;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ticket implements Serializable{
	/**
	 * DataSet名(銘柄コード)
	 */
	private String dsName;
	/**
	 * このチケットに対する新規注文
	 */
	private NewOrder newOrder;
	/**
	 * ストラテジーの名前。
	 */
	private String strategyName;
	/**
	 * ストラテジーごとに一意の番号。
	 */
	private long ticketNumber;
	/**
	 * トレードの結果
	 */
	public TradeResult result;
	/**
	 * このチケットに対する返済注文リスト
	 */
	private List repayOrderList;

	public Ticket(NewOrder newOrder, String strategyName, long ticketNumber) {
		super();
		this.newOrder = newOrder;
		this.dsName = newOrder.getDsName();
		this.strategyName = strategyName;
		this.ticketNumber = ticketNumber;
		this.result = new TradeResult();
		//(設計注)
		//デルタを新規注文とトレード結果の両方で保持している。
		this.result.delta = newOrder.getDelta();
		this.result.lots = newOrder.getLots();
		this.result.ticketNumber = ticketNumber;
		this.result.strategyName = strategyName;
		this.result.securityCode = newOrder.getDsName();
		this.repayOrderList = new ArrayList();
	}

	public String getStrategyName() {
		return strategyName;
	}

	public long getTicketNumber() {
		return ticketNumber;
	}

	public void addRepayOrder(RepayOrder order){
		repayOrderList.add(order);
	}

	public List getRepayOrderList(){
		return repayOrderList;
	}

	public String getDsName() {
		return dsName;
	}

	public NewOrder getNewOrder() {
		return newOrder;
	}

	public boolean isOpened(){
		return result.isOpened;
	}

	public boolean isClosed(){
		return result.isClosed;
	}

	public boolean isExpired(){
		return result.isExpired;
	}

	public boolean isCancellation(){
		return result.isCanceled;
	}

	/**
	 * チケットの新規注文時のデルタを返します。
	 * @return short
	 */
	public short getDelta(){
		return newOrder.getDelta();
	}

	/**
	 * チケットの新規注文時の有効期限の種類を返します。
	 * @return
	 */
	public short getExpired(){
		return newOrder.getExpired();
	}


	/**
	 * チケットの損益を返します。
	 * (設計注)
	 * Ticket対TradeResultの関係を1対nにする場合は
	 * この処理を複数TradeResult対応する必要がある。
	 * @return double
	 */
	public double getProfit(){
		return result.getProfit();
	}

	public void setLeverage(double leverage){
		result.leverage = leverage;
	}

	/**
	 * トレードのコメントを追加する
	 *
	 * @param value コメント
	 */
	public void addComment(String value){
		result.addComment(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((strategyName == null) ? 0 : strategyName.hashCode());
		result = prime * result + (int) (ticketNumber ^ (ticketNumber >>> 32));
		return result;
	}

	/**
	 * strategyNameとticketNumberが同じ場合は同じチケットとみなす。
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ticket other = (Ticket) obj;
		if (strategyName == null) {
			if (other.strategyName != null)
				return false;
		} else if (!strategyName.equals(other.strategyName))
			return false;
		if (ticketNumber != other.ticketNumber)
			return false;
		return true;
	}

}
