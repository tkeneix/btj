package btj.core.dataset;

public class OptionalDataSetFactoryArgs extends CandleDataSetFactoryArgs {
	private String mapDataFileName;
	private int skipEntryCount;
	private MapDataTarget[] mapTargetList;

	public String getMapDataFileName() {
		return mapDataFileName;
	}
	public void setMapDataFileName(String mapDataFileName) {
		this.mapDataFileName = mapDataFileName;
	}
	public int getSkipEntryCount() {
		return skipEntryCount;
	}
	public void setSkipEntryCount(int skipEntryCount) {
		this.skipEntryCount = skipEntryCount;
	}
	public MapDataTarget[] getTarget() {
		return mapTargetList;
	}
	public void setTarget(MapDataTarget[] mapTargetList) {
		this.mapTargetList = mapTargetList;
	}

}
