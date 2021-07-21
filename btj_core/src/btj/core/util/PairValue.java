package btj.core.util;

public class PairValue{
	private String name;
	private double value;

	public PairValue(String name, double value) {
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}

	public String toString(){
		return "name=" + name + " value=" + value;
	}
}
