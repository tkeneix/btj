package btj.core.util;

import java.util.ArrayList;
import java.util.List;

public class StockUtil {

    /**
     * ポジションを分割します
     * @param 全ポジション数
     * @param 単位株数
     * @param 分割数
     * @return 分割されたポジション数（分割数に応じて分割できない要素には0が入ります）
     */
    public static double[] ポジション分割(double 全ポジション数, double 単位株数, int 分割数){
        double[] ret = new double[分割数];
        int i=0;
        while(全ポジション数 > 0){
            ret[i] += 単位株数;
            全ポジション数 -= 単位株数;
            if(i < ret.length - 1){
                i++;
            }else{
                i = 0;
            }
        }

        return ret;
    }


    public static void main(String[] args){
        try{
            double[] ret = ポジション分割(100, 100, 2);
            for(int i=0; i<ret.length; i++){
                System.out.println(ret[i] + " ");
            }
            double tareget = 10000;
            System.out.println(String.valueOf((int)tareget));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
