package btj.core.tester;

public class RepayOrder extends Order{
	/**
	 * 注文価格
	 */
	private double value;
	/**
	 * 返済注文が執行済みか同化を示すフラグ。
	 * 返済注文は複数連ねることができるので、どの返済注文が
	 * 執行されたかを判別するために利用できる。
	 */
	private boolean isExecuted;

	/**
	 * 返済注文クラス
	 * @param dsName
	 * @param type
	 * @param value
	 */
	public RepayOrder(String dsName, short type, double value){
		super(dsName, Order.KD_REPAY, type);
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isExecuted() {
		return isExecuted;
	}

	public void setExecuted(boolean isExecuted) {
		this.isExecuted = isExecuted;
	}

}
