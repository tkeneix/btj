package btj.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DynamicClassLoader extends URLClassLoader {

	public DynamicClassLoader(String url) throws IOException{
		super(new URL[]{new URL(url)});
	}

	/**
	 * URLClassLoaderのクラスロード処理のみを行う
	 */
	public Class loadClass(String name) throws ClassNotFoundException{
		Class ret = null;
		try{
			ret = super.findClass(name);
		}catch(ClassNotFoundException ex){
			ret = Class.forName(name);
		}
		return ret;
	}
}
