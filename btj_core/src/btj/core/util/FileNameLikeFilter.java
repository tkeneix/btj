package btj.core.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileNameLikeFilter implements FilenameFilter{
    public static final short 全て含まれる = (short)0x0001;
    public static final short 少なくとも1つ含まれる = (short)0x0002;

    private String[] likeList;
    private short mode;

    public FileNameLikeFilter(String[] likeList, short mode){
        this.likeList = likeList;
        this.mode = mode;
    }

    public boolean accept(File arg0, String arg1) {
        boolean ret = false;
        if(mode == 全て含まれる){
            ret = true;
            for(int i=0; i<likeList.length; i++){
                if(arg1.indexOf(likeList[i]) == -1){
                    //含まれないものがあった時点でfalseにする
                    ret = false;
                    break;
                }
            }
        }else if(mode == 少なくとも1つ含まれる){
            ret = false;
            for(int i=0; i<likeList.length; i++){
                if(arg1.indexOf(likeList[i]) != -1){
                    //含まれるものがあった時点でtrueにする
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }


}
