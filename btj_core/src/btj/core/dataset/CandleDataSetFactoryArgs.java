package btj.core.dataset;

public class CandleDataSetFactoryArgs extends DataSetFactoryArgs {
	private String dayTermFileName;
	private String dayTermDateFormat;

	public String getDayTermFileName() {
		return dayTermFileName;
	}

	public void setDayTermFileName(String dayTermFileName) {
		this.dayTermFileName = dayTermFileName;
	}

	public String getDayTermDateFormat() {
		return dayTermDateFormat;
	}

	public void setDayTermDateFormat(String dayTermDateFormat) {
		this.dayTermDateFormat = dayTermDateFormat;
	}


}
