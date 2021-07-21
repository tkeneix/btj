/**
 * システムのルールを実装するクラスのインタフェース
 * BackTesterに登録して使用する
 */

package btj.core.strategy;

import btj.core.dataset.DataSetManager;
import btj.core.tester.IOrderManager;

public interface Strategy {
	/**
	 * BackTesterを開始する前に呼ばれる初期化メソッド
	 * @param dsMng
	 * @return なし
	 */
	public void init(DataSetManager dsMng);
	/**
	 * BackTesterから寄付前(足と足の間)に呼ばれるメソッド
	 * @param dsMng
	 * @param ordMng
	 * @param num
	 * @return なし
	 */
	public void ready(DataSetManager dsMng, IOrderManager ordMng, int num);
	/**
	 * BackTesterから足が形成される度に呼ばれるメソッド
	 * numはカレントのカウンタが渡されるため、未来のデータを参照できることがあるので
	 * 注意すること。
	 * 日足ではopen,high,low,closeが未来のデータになり得る。
	 * 分足ではhigh,low,closeが未来のデータになり得る。
	 * (設計注)
	 * numではなくOptionalData#get()のように現在のDataのリファレンスを渡す方法もある。
	 * @param dsMng
	 * @param ordMng
	 * @param num
	 * @return なし
	 */
	public void start(DataSetManager dsMng, IOrderManager ordMng, int num);
	/**
	 * BackTesterを終了する直前に呼ばれる終了メソッド
	 * @param dsMng
	 * @param ordMng
	 * @return なし
	 */
	public void deinit(DataSetManager dsMng, IOrderManager ordMng);
	/**
	 * Strategy名を返します
	 * @return String
	 */
	public String getName();
}
