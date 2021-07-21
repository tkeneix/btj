/**
 * 日足ベースの寄引け～スイング検証用のJudgementクラス
 */

package btj.core.dataset;

import java.util.Iterator;

import btj.core.tester.NewOrder;
import btj.core.tester.Order;
import btj.core.tester.RepayOrder;
import btj.core.tester.Ticket;


public class CandleDayJudgement extends Judgement{
	public Ticket judge(Data data, Ticket ticket){
		CandleData cdata = (CandleData)data;

		if(ticket.result.orderTime == null){
			//初めてこのチケットをチェックする場合は
			//注文時刻を付与する
			ticket.result.orderTime = cdata.date;
		}

		if(!ticket.result.isOpened){
			//オープンしていない場合、新規注文を確認する
			short type = ticket.getNewOrder().getType();
			switch(type){
				case Order.TP_YORI:
				case Order.TP_NARI:
					//寄,成行で新規の場合
					ticket.result.openTime = cdata.date;
					ticket.result.openValue = cdata.open;
					ticket.result.isOpened = true;
					//寄は場中ではないので高値安値で初期化する。
					ticket.result.highTime = cdata.date;
					ticket.result.highValue = cdata.high;
					ticket.result.lowTime = cdata.date;
					ticket.result.lowValue = cdata.low;
					break;
				case Order.TP_HIKE:
					//引で新規の場合
					ticket.result.openTime = cdata.date;
					ticket.result.openValue = cdata.close;
					ticket.result.isOpened = true;
					//high,lowはオープンした時点での値で初期化する。
					ticket.result.highTime = cdata.date;
					ticket.result.highValue = cdata.close;
					ticket.result.lowTime = cdata.date;
					ticket.result.lowValue = cdata.close;
					break;
				case Order.TP_SASHINE:
					//指値の場合
					if(ticket.getNewOrder().getDelta() == NewOrder.DT_BUY){
						//下がったら買う
						if(cdata.low <= ticket.getNewOrder().getValue()){
							ticket.result.openTime = cdata.date;
							ticket.result.openValue = ticket.getNewOrder().getValue();
							ticket.result.isOpened = true;
							//high,lowはオープンした時点での値で初期化する。
							ticket.result.highTime = cdata.date;
							ticket.result.highValue = ticket.getNewOrder().getValue();
							ticket.result.lowTime = cdata.date;
							ticket.result.lowValue = ticket.getNewOrder().getValue();
						}
					}else if(ticket.getNewOrder().getDelta() == NewOrder.DT_SELL){
						//上がったら売る
						if(cdata.high >= ticket.getNewOrder().getValue()){
							ticket.result.openTime = cdata.date;
							ticket.result.openValue = ticket.getNewOrder().getValue();
							ticket.result.isOpened = true;
							//high,lowはオープンした時点での値で初期化する。
							ticket.result.highTime = cdata.date;
							ticket.result.highValue = ticket.getNewOrder().getValue();
							ticket.result.lowTime = cdata.date;
							ticket.result.lowValue = ticket.getNewOrder().getValue();
						}
					}else{
						throw new RuntimeException("予期しないデルタです。delta="
												+ ticket.getNewOrder().getDelta());
					}
					break;
				case Order.TP_GYAKUSASHINE:
					//逆指値の場合
					if(ticket.getNewOrder().getDelta() == NewOrder.DT_BUY){
						//上がったら買う
						if(cdata.open >= ticket.getNewOrder().getValue()){
							//日足は場中に発注できないため、始値による比較も並列して行う
							ticket.result.openTime = cdata.date;
							//寄付の値段で約定する
							ticket.result.openValue = cdata.open;
							ticket.result.isOpened = true;
							//high,lowはオープンした時点での値で初期化する。
							ticket.result.highTime = cdata.date;
							ticket.result.highValue = cdata.open;
							ticket.result.lowTime = cdata.date;
							ticket.result.lowValue = cdata.open;
						}else if(cdata.high >= ticket.getNewOrder().getValue()){
							ticket.result.openTime = cdata.date;
							ticket.result.openValue = ticket.getNewOrder().getValue();
							ticket.result.isOpened = true;
							//high,lowはオープンした時点での値で初期化する。
							ticket.result.highTime = cdata.date;
							ticket.result.highValue = ticket.getNewOrder().getValue();
							ticket.result.lowTime = cdata.date;
							ticket.result.lowValue = ticket.getNewOrder().getValue();
						}
					}else if(ticket.getNewOrder().getDelta() == NewOrder.DT_SELL){
						//下がったら売る
						if(cdata.open <= ticket.getNewOrder().getValue()){
							ticket.result.openTime = cdata.date;
							ticket.result.openValue = cdata.open;
							ticket.result.isOpened = true;
							//high,lowはオープンした時点での値で初期化する。
							ticket.result.highTime = cdata.date;
							ticket.result.highValue = cdata.open;
							ticket.result.lowTime = cdata.date;
							ticket.result.lowValue = cdata.open;
						}else if(cdata.low <= ticket.getNewOrder().getValue()){
							ticket.result.openTime = cdata.date;
							ticket.result.openValue = ticket.getNewOrder().getValue();
							ticket.result.isOpened = true;
							//high,lowはオープンした時点での値で初期化する。
							ticket.result.highTime = cdata.date;
							ticket.result.highValue = ticket.getNewOrder().getValue();
							ticket.result.lowTime = cdata.date;
							ticket.result.lowValue = ticket.getNewOrder().getValue();
						}
					}else{
						throw new RuntimeException("予期しないデルタです。delta="
												+ ticket.getNewOrder().getDelta());
					}
					break;
				default:
					throw new RuntimeException("予期しないタイプです。type="
							+ ticket.getNewOrder().getType());
			}
		}

		if(ticket.result.isOpened){
			//high,lowを更新する。
			JudgementUtil.updateHighValue(ticket, cdata);
			JudgementUtil.updateLowValue(ticket, cdata);

			//既にオープンしている場合、返済注文を先頭から順に確認する。
			//確認が取れたところで返済注文が残っていても抜ける。
			Iterator rIte = ticket.getRepayOrderList().iterator();
			while(rIte.hasNext()){
				RepayOrder rorder = (RepayOrder)rIte.next();
				short type = rorder.getType();
				switch(type){
					case Order.TP_YORI:
					case Order.TP_NARI:
						//寄で返済の場合
						/**
						 * 日足で成行注文を出した場合にopenを使うかcloseを使うは
						 * 検討の余地あり。少なくともChannelProfileを日足で使う場合は
						 * open前提である。
						 */
						ticket.result.closeTime = cdata.date;
						ticket.result.closeValue = cdata.open;
						ticket.result.isClosed = true;
						rorder.setExecuted(true);
						break;
					case Order.TP_HIKE:
						//引で返済の場合
						ticket.result.closeTime = cdata.date;
						ticket.result.closeValue = cdata.close;
						ticket.result.isClosed = true;
						rorder.setExecuted(true);
						break;
					case Order.TP_SASHINE:
						//指値の場合
						if(ticket.getNewOrder().getDelta() == NewOrder.DT_SELL){
							//売りで入って下がったら買い戻す
							if(cdata.low <= rorder.getValue()){
								ticket.result.closeTime = cdata.date;
								ticket.result.closeValue = rorder.getValue();
								ticket.result.isClosed = true;
								rorder.setExecuted(true);
							}
						}else if(ticket.getNewOrder().getDelta() == NewOrder.DT_BUY){
							//買いで入って上がったら売る
							if(cdata.high >= rorder.getValue()){
								ticket.result.closeTime = cdata.date;
								ticket.result.closeValue = rorder.getValue();
								ticket.result.isClosed = true;
								rorder.setExecuted(true);
							}
						}else{
							throw new RuntimeException("予期しないデルタです。delta="
													+ ticket.getNewOrder().getDelta());
						}
						break;
					case Order.TP_GYAKUSASHINE:
						//逆指値の場合
						if(ticket.getNewOrder().getDelta() == NewOrder.DT_SELL){
							//売りで入って上がったら買う
							if(cdata.high >= rorder.getValue()){
								ticket.result.closeTime = cdata.date;
								ticket.result.closeValue = rorder.getValue();
								ticket.result.isClosed = true;
								rorder.setExecuted(true);
							}
						}else if(ticket.getNewOrder().getDelta() == NewOrder.DT_BUY){
							//買いで入って下がったら売る
							if(cdata.low <= rorder.getValue()){
								ticket.result.closeTime = cdata.date;
								ticket.result.closeValue = rorder.getValue();
								ticket.result.isClosed = true;
								rorder.setExecuted(true);
							}
						}else{
							throw new RuntimeException("予期しないデルタです。delta="
													+ ticket.getNewOrder().getDelta());
						}
						break;
					default:
						throw new RuntimeException("予期しないタイプです。type="
								+ ticket.getNewOrder().getType());
				}
				if(rorder.isExecuted()){
					break;
				}
			}
		}
		return ticket;
	}

	public void checkNewOrder(Data data, Ticket ticket){
		CandleData cdata = (CandleData) data;
		if(ticket.getDelta() == NewOrder.DT_BUY){
			if(ticket.getNewOrder().getType() == Order.TP_SASHINE){
				//下がったら買うので、openが指定値よりも下の場合はエラー
				if(cdata.open < ticket.getNewOrder().getValue()){
					throw new RuntimeException("Error: NewOrder TP_SASHINE="
							+ ticket.getNewOrder().getValue()
							+ " DeltaType=" + ticket.getDelta()
							+ " open=" + cdata.open);
				}
			}else if(ticket.getNewOrder().getType() == Order.TP_GYAKUSASHINE){
				//上がったら買うので、openが指定値よりも上の場合はエラー
				//(修正)
				//寄付前に逆指値を出した場合、openが逆指値よりも高くよったとすると
				//openで約定される。
//				if(cdata.open > ticket.getNewOrder().getValue()){
//					throw new RuntimeException("Error: NewOrder TP_GYAKUSASHINE="
//							+ ticket.getNewOrder().getValue()
//							+ " DeltaType=" + ticket.getDelta()
//							+ " open=" + cdata.open);
//				}
			}
		}else if(ticket.getDelta() == NewOrder.DT_SELL){
			if(ticket.getNewOrder().getType() == Order.TP_SASHINE){
				//上がったら売るので、openが指定値よりも上の場合はエラー
				if(cdata.open > ticket.getNewOrder().getValue()){
					throw new RuntimeException("Error: NewOrder TP_SASHINE="
							+ ticket.getNewOrder().getValue()
							+ " DeltaType=" + ticket.getDelta()
							+ " open=" + cdata.open);
				}
			}else if(ticket.getNewOrder().getType() == Order.TP_GYAKUSASHINE){
				//下がったら売るので、openが指定値よりも下の場合はエラー
				//(修正)
				//寄付前に逆指値を出した場合、openが逆指値よりも低くよったとすると
				//openで約定される。
				//				if(cdata.open < ticket.getNewOrder().getValue()){
//					throw new RuntimeException("Error: NewOrder TP_GYAKUSASHINE="
//							+ ticket.getNewOrder().getValue()
//							+ " DeltaType=" + ticket.getDelta()
//							+ " open=" + cdata.open);
//				}
			}
		}else{
			throw new RuntimeException("Error: Unknown DeltaType=" + ticket.getDelta());
		}

		ticket.getNewOrder().setCheck(true);
	}

	public void checkRepayOrder(Data data, Ticket ticket){
		CandleData cdata = (CandleData) data;
		Iterator repayList = ticket.getRepayOrderList().iterator();
		while(repayList.hasNext()){
			RepayOrder repay = (RepayOrder)repayList.next();
			//チェック済みなら次のRepayOrderを確認する
			if(repay.getCheck()) continue;

			if(ticket.getDelta() == NewOrder.DT_BUY){
				if(repay.getType() == Order.TP_SASHINE){
					//上がったら売る、openが指定値よりも上の場合はエラー
					if(cdata.open > repay.getValue()){
						throw new RuntimeException("Error: RepayOrder TP_SASHINE="
								+ repay.getValue()
								+ " DeltaType=" + ticket.getDelta()
								+ " open=" + cdata.open);
					}
				}else if(repay.getType() == Order.TP_GYAKUSASHINE){
					//下がったら売るので、openが指定値よりも下の場合はエラー
//					if(cdata.open < repay.getValue()){
//						throw new RuntimeException("Error: RepayOrder TP_GYAKUSASHINE="
//								+ repay.getValue()
//								+ " DeltaType=" + ticket.getDelta()
//								+ " open=" + cdata.open);
//					}
				}
			}else if(ticket.getDelta() == NewOrder.DT_SELL){
				if(repay.getType() == Order.TP_SASHINE){
					//下がったら買うので、openが指定値よりも下の場合はエラー
					if(cdata.open < repay.getValue()){
						throw new RuntimeException("Error: RepayOrder TP_SASHINE="
								+ repay.getValue()
								+ " DeltaType=" + ticket.getDelta()
								+ " open=" + cdata.open);
					}
				}else if(repay.getType() == Order.TP_GYAKUSASHINE){
					//上がったら買うので、openが指定値よりも上の場合はエラー
//					if(cdata.open > repay.getValue()){
//						throw new RuntimeException("Error: RepayOrder TP_GYAKUSASHINE="
//								+ repay.getValue()
//								+ " DeltaType=" + ticket.getDelta()
//								+ " open=" + cdata.open);
//					}
				}
			}else{
				throw new RuntimeException("Error: Unknown DeltaType=" + ticket.getDelta());
			}

			repay.setCheck(true);
		}
	}

}
