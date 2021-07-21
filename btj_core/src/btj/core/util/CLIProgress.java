package btj.core.util;

public class CLIProgress {
    int max;
    long initialTimeLong;
    String beforeDisplayStr;

    public CLIProgress(int max, long initialTimeLong){
        try{
            this.max = max;
            this.initialTimeLong = initialTimeLong;
            this.beforeDisplayStr = "";
            StringBuilder sb = new StringBuilder();
            sb.append("進捗状況：");
            sb.append(0).append("/").append(max).append(" ");
            sb.append("[").append(0).append("%] ");
            sb.append("初回実施中");
            cleanDisplay();
            System.out.print(sb.toString());
            beforeDisplayStr = sb.toString();
        }catch(Exception ex){};
    }

    public void update(int current, long currentTimeLong){
        try{
            StringBuilder sb = new StringBuilder();
            sb.append("進捗状況：");
            sb.append(current).append("/").append(max).append(" ");
            int percent = (int)((current != 0 ? (double)current / (double)max : 0) * 100);
            sb.append("[").append(percent).append("%] ");
            long これまで要した時間msec = currentTimeLong - initialTimeLong;
            int これまで実行した回数 = current + 1;
            int 残りの実行回数 = max - current;
            double これから要する時間msec = これまで要した時間msec != 0.0 ?
                                                                 (これまで要した時間msec / これまで実行した回数) * (残りの実行回数) :
                                                                 0.0;
            sb.append("残り ").append(String.format("%.2f", これから要する時間msec / 1000 / 60)).append(" 分");

            cleanDisplay();
            System.out.print(sb.toString());
            beforeDisplayStr = sb.toString();
        }catch(Exception ex){}
    }

    public void finish(){
        try{
            cleanDisplay();
            System.out.println("進捗状況：完了 "
                    + String.format("%.2f", (double)((System.currentTimeMillis() -  initialTimeLong)/ 1000 / 60))
                    + "分かかりました。");
        }catch(Exception ex){}
    }

    public void cleanDisplay(){
        System.out.print("\r");
        for(int i=0; i<this.beforeDisplayStr.length(); i++){
            System.out.print(" ");
        }
        System.out.print("\r");
    }
}
