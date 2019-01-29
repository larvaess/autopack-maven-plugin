package com.larva.plugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
	
	private static class SingletonClassInstance {
		private static final PropertiesUtils instance = new PropertiesUtils();
	}

	private PropertiesUtils(){}

	public static PropertiesUtils getInstance() {
		return SingletonClassInstance.instance;
	}
	
    synchronized public Properties loadProps(String path){
    	Properties props = new Properties();
		InputStream in = null;
		try {
			File file = new File(path);
			//要加载的属性文件
			props.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.out.println("路径：" + path + "下文件未找到");
		} catch (IOException e) {
			System.out.println("出现IOException");
		} catch (Exception e) {
			System.out.println("出现Exception"+e.getLocalizedMessage());
		} finally {
			try {
				if(null != in) {
					in.close();
				}
			} catch (IOException e) {
				System.out.println("文件流关闭出现异常");
			}
		}
        return props;
    }
}
