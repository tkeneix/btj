/**
 * DataSetFactory#create()の引数に渡すクラス
 * 本来はコンストラクタでメンバ変数を受けるようにし
 * getterのみ提供してカプセル化する方がよい。
 * しかし、コンストラクタへ設定するコーディングだと
 * 何の値を設定しているのか、わかり辛くなるデメリットがある。
 */

package btj.core.dataset;

import java.util.Date;

public class DataSetFactoryArgs {
	private String dsName;
	private Date startDate;
	private Date endDate;
	private Judgement judgement;

	public String getDsName() {
		return dsName;
	}
	public void setDsName(String dsName) {
		this.dsName = dsName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Judgement getJudgement() {
		return judgement;
	}
	public void setJudgement(Judgement judgement) {
		this.judgement = judgement;
	}

}
