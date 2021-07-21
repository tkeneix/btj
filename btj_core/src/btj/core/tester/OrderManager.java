package btj.core.tester;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import btj.core.dataset.DataSet;
import btj.core.dataset.DataSetManager;
import btj.core.util.DateFormat;


public class OrderManager implements IOrderManager {
	/**
	 * key=Strategy名, value=そのStrategyのチケット通番
	 */
	private Map ticketNumberTable = new HashMap();

	/**
	 * key=Strategy名, value=そのStrategyのチケットリスト
	 * 新規注文した順番にソートされている。
	 */
	private Map ticketTable = new HashMap();

	/**
	 * 約定済みのStrategyごとのチケットリスト。
	 * 約定した順番にソートされている。
	 */
	private Map historicalTicketTable = new HashMap();

	private FileManager fileMng;

	private DataSetManager dsMng;

	/**
	 * 注文を受け付けた場合にフラグをtrueにする
	 */
	private boolean checkedNewOrder = false;
	private boolean checkedRepayOrder = false;

	public OrderManager(FileManager fileMng, DataSetManager dsMng){
		this.fileMng = fileMng;
		this.dsMng = dsMng;
	}


	/**
	 * Strategyごとのチケット通番を発行する。
	 * @param newOrder
	 * @param strategy
	 * @return
	 */
	private Ticket createTicket(NewOrder newOrder, String strategy){
		Ticket ret = null;
		Long currentNum = (Long)ticketNumberTable.get(strategy);
		if(currentNum == null){
			currentNum = new Long(1);
		}
		ret = new Ticket(newOrder, strategy, currentNum);
		ticketNumberTable.put(strategy, new Long(currentNum + 1));
		return ret;
	}

	/*
	 * @see tester.IOrderManager#requestNewOrder(java.lang.String, java.lang.String, short, short, double, double, short)
	 */
	@Override
	public Ticket requestNewOrder(String strategy, String dsName, short type,
								short delta, double lots, double value, short expired){

		Ticket ret = null;
		NewOrder norder = new NewOrder(dsName, type, delta, lots, value, expired);
		ret = createTicket(norder, strategy);

		//OrderとTicketを対応づける
		norder.setTicket(ret);

		//Strategyがテーブルに登録されていなければチケットリストを生成する
		ArrayList tkList = (ArrayList)ticketTable.get(strategy);
		if(tkList == null){
			tkList = new ArrayList();
		}
		tkList.add(ret);
		ticketTable.put(strategy, tkList);
		checkedNewOrder = true;
		return ret;
	}

	/*
	 * @see tester.IOrderManager#requestNewOrder(java.lang.String, java.lang.String, short, short, double, short)
	 */
	@Override
	public Ticket requestNewOrder(String strategy, String dsName, short type,
								short delta, double value, short expired){
		return requestNewOrder(strategy, dsName, type, delta, 1.0, value, expired);
	}

	/*
	 * @see tester.IOrderManager#requestRepayOrder(tester.Ticket, short, double)
	 */
	@Override
	public Ticket requestRepayOrder(Ticket ticket, short type, double value){
		RepayOrder rorder = new RepayOrder(ticket.getDsName(), type, value);
		ticket.addRepayOrder(rorder);
		checkedRepayOrder = true;
		return ticket;
	}


	/*
	 * @see tester.IOrderManager#requestCancel(tester.Ticket)
	 */
	@Override
	public Ticket requestCancel(Ticket ticket){
		ticket.result.isCanceled = true;
		ticket.result.isExpired = true;

		return ticket;
	}

	/*
	 * @see tester.IOrderManager#nextCheck(int)
	 */
	@Override
	public OrderCheckStatus nextCheck(int num){
		OrderCheckStatus ret = null;
		boolean isEnd = false;
		List closedList = new ArrayList();

		//ストラテジごとのチケットリストをループさせる
		Iterator stIte = ticketTable.values().iterator();
		while(stIte.hasNext()){
			List tkList = (List)stIte.next();
			List clonetList = (ArrayList)((ArrayList)tkList).clone();
			Iterator tkIte = clonetList.iterator();
			//チケット全てをチェックする
			for(int count=0; tkIte.hasNext(); count++){
				Ticket ticket = (Ticket)tkIte.next();
				DataSet ds = dsMng.getLabelDataset(ticket.getDsName());
				if(ds.hasNum(num)){
					//新規Orderチェック
					if(checkedNewOrder){
						ds.checkNewOrder(ticket, num);
					}
					if(checkedRepayOrder){
						ds.checkRepayOrder(ticket, num);
					}

					//judgeする前にキャンセルされている場合がある
					//キャンセルされている場合は、Orderチェックしない。
					if(!ticket.result.isCanceled){
						ticket = ds.judge(ticket, num);
					}

					//チケットクローズしていた場合
					//またはチケット有効期限が切れていた場合
					if(ticket.result.isClosed || ticket.result.isExpired){
						//クローズ済みリストへ登録する
						closedList.add(ticket);
						//チケットリストから削除する
						//(設計注)
						//検索効率がよろしくなさそうなのでHashMapで管理した方がよいかも。
						tkList.remove(tkList.indexOf(ticket));
						//ヒストリカルテーブルの方へ登録する
						ArrayList histTkList = (ArrayList)historicalTicketTable.get(ticket.getStrategyName());
						if(histTkList == null){
							histTkList = new ArrayList();
						}
						histTkList.add(ticket);
						historicalTicketTable.put(ticket.getStrategyName(), histTkList);
					}
				}else{
					//一つでもDataSetが終端になった場合は検証を終了する
					isEnd = true;
					break;
				}
			}
			if(isEnd){
				break;
			}
		}

		if(checkedNewOrder){
			checkedNewOrder = false;
		}

		if(checkedRepayOrder){
			checkedRepayOrder = false;
		}

		return new OrderCheckStatus(isEnd ? OrderCheckStatus.FINISHED
						: OrderCheckStatus.PROCESSING, closedList);
	}

	public boolean requestRepayAllOrder(String strategy){
		boolean ret = false;
		if(true){
			throw new RuntimeException("未サポート機能です");
		}
		return ret;
	}
	/*
	 * @see tester.IOrderManager#dumpHistoricalTicketTable()
	 */
	@Override
	public void dumpHistoricalTicketTable(){
		try {
			Iterator stIte = historicalTicketTable.keySet().iterator();
			while(stIte.hasNext()){
				String keyName = (String)stIte.next();

				String fName = keyName
								+ "_"
								+ "deallog.csv";
				PrintWriter pw = fileMng.getSubDir(fName);
				pw.println(TradeResult.toCSVHeader());

				List tkList = (List)historicalTicketTable.get(keyName);
				Iterator tkIte = tkList.iterator();
				while(tkIte.hasNext()){
					pw.println(((Ticket)tkIte.next()).result.toCSV());
				}
				pw.flush();
				pw.close();
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	/*
	 * @see tester.IOrderManager#dumpStdTicketTable()
	 */
	@Override
	public void dumpStdTicketTable(){
		try {
			Iterator stIte = ticketTable.keySet().iterator();
			while(stIte.hasNext()){
				String keyName = (String)stIte.next();
				System.out.println(TradeResult.toCSVHeader());
				List tkList = (List)ticketTable.get(keyName);
				Iterator tkIte = tkList.iterator();
				while(tkIte.hasNext()){
					System.out.println(((Ticket)tkIte.next()).result.toCSV());
				}
			}
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	/*
	 * @see tester.IOrderManager#shutdown()
	 */
	@Override
	public void shutdown(){
		ticketNumberTable.clear();
		ticketTable.clear();
		historicalTicketTable.clear();
	}

}
