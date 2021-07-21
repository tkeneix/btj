package gridsample;

import java.io.Serializable;

public interface Service {
	Serializable init(Serializable value);
	Serializable execute(Serializable value);
	Serializable deinit(Serializable value);
}
