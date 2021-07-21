package gridsample;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class SampleService implements Service {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Random rand = null;

	public Serializable init(Serializable value) {
		rand = new Random();
		System.out.println("init " + value.toString());
		return value;
	}

	public Serializable execute(Serializable value) {
		int count = (int)(rand.nextDouble() * 100.0);
		for(int i=0; i<count; i++){}
		String message = sdf.format(new Date()) + ",execute," + value.toString() + "," + count;
		System.out.println(message);
		return message;
	}

	public Serializable deinit(Serializable value) {
		System.out.println("deinit " + value.toString());
		return value;
	}

}
