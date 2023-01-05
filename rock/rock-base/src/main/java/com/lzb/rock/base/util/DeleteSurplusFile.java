package com.lzb.rock.base.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteSurplusFile {

	public static void main(String[] args) {

		List<String> deleteFileName = new ArrayList<String>();

		deleteFileName.add(".settings");
		deleteFileName.add("target");
		deleteFileName.add(".classpath");
		deleteFileName.add(".project");
		deleteFileName.add(".git");
		deleteFileName.add(".mvn");
		deleteFileName.add(".svn");
		deleteFileName.add(".git");

		List<String> filterFileName = new ArrayList<String>();

		filterFileName.add("mycat");

		List<String> pathnames = new ArrayList<String>();
		// pathnames.add("D:\\java\\eclipse\\eclipse-workspace\\canal");
		pathnames.add("D:\\工作\\guns");
		for (String pathname : pathnames) {
			DeleteSurplusFile.delete(new File(pathname), deleteFileName, filterFileName);
		}
	}

	public static void delete(File file, List<String> deleteFileName, List<String> filterFileName) {

		if (filterFileName.contains(file.getName())) {
			return;
		}

		if (deleteFileName.contains(file.getName())) {
			log.info(file.getAbsolutePath());
			if (file.isFile()) {
				deleteFile(file);
				return;
			} else {
				deleteDirectory(file);
				return;
			}
		}
		if (file.isDirectory()) {
			File[] ff = file.listFiles();
			for (File file2 : ff) {
				delete(file2, deleteFileName,filterFileName);
			}

		}

	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath 被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static void deleteFile(File file) {
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
		}
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath 被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static void deleteDirectory(File file) {
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!file.exists() || !file.isDirectory()) {
			return;
		}
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				deleteFile(files[i]);
			} else {
				deleteDirectory(files[i]);
			}
		}
		file.delete();
	}

}
