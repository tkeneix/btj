/**
 * デリバティブ関連ユーティリティ
 * boostで作成したC++ライブラリのJavaラッパークラス
 * ExcelVBAからboostへの移植作業で断念
 * sqlite3のbackupAPIをCでしか記述できない「重要」と考えており
 * そのためにユーティリティ群のboost化を図っていた。
 * backupAPIがJavaから使用できるのなら、今更boostにする必要もない。
 */

package btj.core.util;

//import com.sun.jna.Library;
//import com.sun.jna.Native;
//
//public interface DerivativeUtilInterface extends Library{
//	DerivativeUtilInterface INSTANCE = (DerivativeUtilInterface)Native.loadLibrary("derivativeUtil", DerivativeUtilInterface.class);
//
public interface DerivativeUtilInterface{
	DerivativeUtilInterface INSTANCE = null;

	int kizamineOfStrikePrice(String targetDate);
	int calcATMLevel(String targetDate, int value, int level);
	int calcATMUpper(String targetDate, int value);
	int calcATMLower(String targetDate, int value);
	int calcATM(String targetDate, int value);
	int isHoliday(String targetDate);
	void initHolidaylist();
}
