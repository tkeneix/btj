package btj.core.dataset;

import java.util.Date;
import java.util.Map;

public class MapData extends Data {
	private MapDataTarget[] target;//順序性を識別するため
	private Date date;
	private Map table;

	public MapData(MapDataTarget[] target, Date date, Map table) {
		super();
		this.target = target;
		this.date = date;
		this.table = table;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Map getTable() {
		return table;
	}

	public void setTable(Map table) {
		this.table = table;
	}

	public MapDataTarget[] getTarget() {
		return target;
	}

	public void setTarget(MapDataTarget[] target) {
		this.target = target;
	}


}
