/**
 * File関連ユーティリティ
 *
 * Attention:
 * FilenameFilter#accept()はファイルに到達した場合に動作し、acceptの引数のnameは拡張子を含まない。
 */

package btj.core.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	public static FilenameFilter ALWAYS_SAME = new AlwaysSameFilter();

	public static File[] getFileListIncludeSubdir(String path, FilenameFilter filter){
		List<File> results = new ArrayList<File>();
		_getFileListIncludeSubdir(new File(path), filter, results);
		File[] ret = new File[results.size()];
		results.toArray(ret);
		return ret;
	}

	private static void _getFileListIncludeSubdir(File fPath, FilenameFilter filter, List<File> results){

		File[] subdirs = fPath.listFiles(filter);
		for(int i=0; i<subdirs.length; i++){
			if(subdirs[i].isDirectory()){
				_getFileListIncludeSubdir(subdirs[i], filter, results);
			}else{
				results.add(subdirs[i]);
			}
		}
	}


	public static class AlwaysSameFilter implements FilenameFilter{
		@Override
		public boolean accept(File arg0, String arg1) {
			return true;
		}
	}

	public static class LastIndexOfFilter implements FilenameFilter{
		private String name;

		public LastIndexOfFilter(String name){
			this.name = name;
		}

		@Override
		public boolean accept(File arg0, String arg1) {
			boolean ret = false;
			if(arg1.lastIndexOf(name) != -1){
				//System.out.println(arg1);
				ret = true;
			}

			return ret;
		}
	}


	public static void main(String[] args){
		String path = "Z:/WK/DataSource/東証/海外投資家地域別株券売買状況/chiikibetsu/海外投資家地域別_m";
		File[] lists = getFileListIncludeSubdir(path, new FileUtil.LastIndexOfFilter("fivst"));

		for(int i=0; i<lists.length; i++){
			System.out.println(lists[i]);
		}

		System.out.println("finished.");
	}
}
