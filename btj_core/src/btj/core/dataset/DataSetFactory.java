package btj.core.dataset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

public interface DataSetFactory {
	public DataSet create(DataSetFactoryArgs args)
			throws FileNotFoundException, IOException, ParseException;
}
