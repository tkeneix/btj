package btj.core.dataset;

import java.util.Date;

import btj.core.tester.Ticket;


public abstract class DataSet {

	private String name;
	private String filePath;
	private Date startDate;
	private Date endDate;
	private Judgement judgement;

	public DataSet(String name, String filePath, Date startDate,
			Date endDate, Judgement judgement) {
		super();
		//if(name == null || filePath == null || startDate == null
		//		|| endDate == null){
		//	throw new RuntimeException("DataSetのコンストラクタのどれかにnullが設定されています。");
		//}
		if(name == null || filePath == null){
			throw new RuntimeException("DataSetのコンストラクタのどれかにnullが設定されています。");
		}
		this.name = name;
		this.filePath = filePath;
		this.startDate = startDate;
		this.endDate = endDate;
		this.judgement = judgement;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getFilePath() {
		return filePath;
	}

	public Judgement getJudgement(){
		return judgement;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Ticket judge(Ticket ticket, int num){
		return judgement.judge(get(num), ticket);
	}

	public void checkNewOrder(Ticket ticket, int num){
		judgement.checkNewOrder(get(num), ticket);
	}

	public void checkRepayOrder(Ticket ticket, int num){
		judgement.checkRepayOrder(get(num), ticket);
	}

	public boolean hasNum(int num){
		return num >= length() ? false : true;
	}

	public abstract boolean hasNext();
	public abstract Data next();
	public abstract Data get(int num);
	public abstract void add(Data value);
	public abstract void padding(int num, Data d);
	public abstract int length();
	public abstract int offset();
	public abstract void clearOffset();
	public abstract void close();
}