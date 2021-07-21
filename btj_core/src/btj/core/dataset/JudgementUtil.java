

package btj.core.dataset;

import btj.core.tester.Ticket;
import btj.core.util.MathUtil;

public class JudgementUtil {
	public static final short ST_OPEN	=	1;
	public static final short ST_CLOSE	=	2;

	public static void updateHighValue(Ticket ticket, CandleData cdata){
		ticket.result.highValue = MathUtil.max(cdata.high, ticket.result.highValue);
		if(ticket.result.highValue == cdata.high){
			//更新されている場合
			ticket.result.highTime = cdata.date;
		}
	}

	public static void updateLowValue(Ticket ticket, CandleData cdata){
		ticket.result.lowValue = MathUtil.min(cdata.low, ticket.result.lowValue);
		if(ticket.result.lowValue == cdata.low){
			//更新されている場合
			ticket.result.lowTime = cdata.date;
		}
	}


	/**
	 * (設計注)
	 * Judgementクラスの処理を共通化しようとしたが良いアイディア
	 * が出なかった。本メソッドは現状使用していない。
	 */
	public static void setOpen(Ticket ticket, CandleData cdata, short type){
		switch(type){
		case ST_OPEN:
			ticket.result.openTime = cdata.date;
			ticket.result.openValue = cdata.open;
			ticket.result.highTime = cdata.date;
			ticket.result.highValue = cdata.high;
			ticket.result.lowTime = cdata.date;
			ticket.result.lowValue = cdata.low;
			ticket.result.isOpened = true;
			break;
		case ST_CLOSE:


			break;
		default:
			throw new RuntimeException("予期しないセット識別子です。");
		}
	}
}
