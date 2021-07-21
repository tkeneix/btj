package btj.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import btj.common.Job;

import btj.core.strategy.Strategy;

public class StrategyFactory {
	private static StrategyFactory factory;
	private Map<String,DynamicClassLoader> urlMap;

	public synchronized static StrategyFactory getInstance(){
		if(factory == null){
			factory = new StrategyFactory();
		}
		return factory;
	}

	protected StrategyFactory(){
		this.urlMap = new HashMap<String,DynamicClassLoader>();
	}

	public synchronized Strategy create(Job job) throws IOException,
												ClassNotFoundException,
												InstantiationException,
												IllegalAccessException,
												SecurityException,
												NoSuchFieldException{
		DynamicClassLoader loader = urlMap.get(job.getJarUrl());
		if(loader == null){
			loader = new DynamicClassLoader(job.getJarUrl());
			/**
			 * ClassLoaderを再利用しようとすると以下が発生する
			 * java.lang.LinkageError: loader (instance of  btj/service/DynamicClassLoader): attempted  duplicate class definition for name:～
			 */
			//urlMap.put(job.getJarUrl(), loader);
		}

		Class stgCls = loader.loadClass(job.getClassName());
		Strategy stgImpl = (Strategy)stgCls.newInstance();

		Iterator fite = job.getFieldMap().keySet().iterator();
		while(fite.hasNext()){
			String name = (String)fite.next();
			Field field = stgCls.getDeclaredField(name);
			if(field == null){
				throw new RuntimeException("ClassとfieldMapのミスマッチ clsName="
							+ job.getClassName() + " map=" + job.getFieldMap().toString());
			}
			field.setAccessible(true);
			field.set(stgImpl, job.getFieldMap().get(name));
		}

		return stgImpl;
	}

	public synchronized void clearLoader(){
		urlMap.clear();
	}

	public synchronized int getUrlMapCount(){
		return urlMap.size();
	}
}
