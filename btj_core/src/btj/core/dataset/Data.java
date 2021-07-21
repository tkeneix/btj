package btj.core.dataset;

public class Data {
	private boolean isAvailable = true;

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public String toCSV(){
		return "To do overwride.";
	}
}
