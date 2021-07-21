package btj.core.log;

public interface ISimpleLogger {

	public abstract void write(String message);

	public abstract void close();

}