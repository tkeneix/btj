package btj.core.log;

public interface IMemoryLogger {
	public abstract void put(String key, Object value);
	public abstract Object get(String key);
	public abstract Object remove(String key);
	public abstract void dump();
}
