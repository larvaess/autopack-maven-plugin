package com.larva.plugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.maven.plugin.logging.SystemStreamLog;

public class FileUtils extends SystemStreamLog{
	
	private static class SingletonClassInstance {
		private static final FileUtils instance = new FileUtils();
	}

	private FileUtils(){}

	public static FileUtils getInstance() {
		return SingletonClassInstance.instance;
	}
	
	/***
	 * 清空目录,是否保留根目录
	 * @param srcFile
	 */
	public void deleteFiles(File srcFile,boolean saveSrc) {
        if (srcFile.exists()) {
        	String srcFileName  = srcFile.getName();
            //存放文件夹
            LinkedList<File> directories = new LinkedList<File>();
            ArrayList<File> directoryList = new ArrayList<File>();
            do {
                File[] files = directories.size() > 0 ? directories.removeFirst().listFiles() : new File[]{srcFile};
                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            directories.add(f);
                            directoryList.add(f);
                        } else {
                            f.delete();
                        }
                    }
                }
            } while (directories.size() > 0);
            //这时所有非文件夹都删光了，可以直接删文件夹了(注意从后往前遍历)
            for (int i = directoryList.size() - 1; i >= 0; i--) {
            	String directoryName = directoryList.get(i).getName();
            	if (!srcFileName.equals(directoryName) || !saveSrc) {
            		// 删除
            		directoryList.get(i).delete();
            	}
            }
        }
    }
	
	/***
	 * 清除某目录下的空文件夹
	 * @param dir
	 */
	public void clear( File dir ){
        File[] dirs = dir.listFiles();
        for( int i = 0; i < dirs.length; i++ ) {
            if( dirs[i].isDirectory() ) {
                clear( dirs[i] );
            }
        }
        if ( dir.isDirectory() ) {
        	dir.delete();
        }
    }
	
	
	/** 
	* 复制整个文件夹内容 
	* @param oldPath String 原文件路径 如：c:/fqf 
	* @param newPath String 复制后路径 如：f:/fqf/ff 
	* @return boolean 
	*/ 
	public void copyFolder(String oldPath, String newPath) { 
		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			error("复制整个文件夹内容操作出错" + e.getLocalizedMessage());
		}
	}
	
}
