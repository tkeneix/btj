package btj.core.dataset;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public interface DataStream {
	public Data next() throws IOException, ParseException;
	public void back(Data data);
}
