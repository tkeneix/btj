package btj.core.dataset;

public class MapDataTarget {
	private String keyName;
	private int column;

	public MapDataTarget(String keyName, int column) {
		super();
		this.keyName = keyName;
		this.column = column;
	}

	public MapDataTarget(String properties) {
		super();
		String[] array = properties.split("/");
		if(array == null || array.length != 2){
			throw new RuntimeException("Error: MapDataTarget args = " + properties);
		}
		this.keyName = array[0].trim();
		this.column = Integer.parseInt(array[1].trim());
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

}
