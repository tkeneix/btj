package btj.core.log;

public interface ILogger {

	public static final short DEBUG = (short)0;
	public static final short INFO = (short) 1;
	public static final short WARN = (short) 2;
	public static final short ERR = (short) 3;

	public abstract void write(short level, String message);

	public abstract void close();

}