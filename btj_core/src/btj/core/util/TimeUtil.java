package btj.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtil {
	public static final short DIFF_SEC = 1;
	public static final short DIFF_MINUTES = 2;
	public static final short DIFF_HOUR = 3;
	public static final short DIFF_DAY = 4;
	public static final short DIFF_WEEK = 5;
	public static final short DIFF_MONTH = 6;

	public static final short BA_OTHER = 0x0000;
	public static final short BA_ZENBA = 0x0001;
	public static final short BA_GOBA  = 0x0002;
	public static final short BA_YUUBA = 0x0004;//3は欠番。SessionDataSetFactoryと関連している。

	private static final int BA_ZENBA_STARTNUM = 900;
	private static final int BA_ZENBA_ENDNUM = 1110;
	private static final int BA_GOBA_STARTNUM = 1230;
	private static final int BA_GOBA_ENDNUM = 1510;
	private static final int BA_YUUBA_STARTNUM = 1630;
	private static final int BA_YUUBA_ENDNUM = 300;

	public static final long MSEC_PERDAY = 1000 * 60 * 60 * 24;

	/**
	 * (設計注)
	 * 設計を含めてから改善の余地あり。
	 * @param before
	 * @param current
	 * @param kind
	 * @return
	 */
	public static int dateDiff(Date before, Date current, short kind){
		int ret = 0;
		GregorianCalendar bCal = new GregorianCalendar();
		GregorianCalendar cCal = new GregorianCalendar();
		bCal.setTime(before);
		cCal.setTime(current);

		int b;
		int c;
		switch(kind){
			case DIFF_DAY:
				b = bCal.get(Calendar.MONTH);
				c = cCal.get(Calendar.MONTH);
				if(b > c){
					ret = -1;
					break;
				}else if(b < c){
					ret = 1;
					break;
				}

				b = bCal.get(Calendar.DAY_OF_MONTH);
				c = cCal.get(Calendar.DAY_OF_MONTH);
				if(b > c){
					ret = -1;
					break;
				}else if(b < c){
					ret = 1;
					break;
				}
			default:
				break;
		}

		return ret;
	}

	public static  int calendarGet(Date date, int kind){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal.get(kind);
	}

	/**
	 * 前場、後場、夕場かどうかをチェックしそれぞれのステータスを返す。
	 * @param date
	 * @return
	 */
	public static short checkSession(Date date){
		short ret = BA_OTHER;
		int dateNum = calendarGet(date, Calendar.HOUR_OF_DAY) * 100
					+ calendarGet(date, Calendar.MINUTE);
		if(dateNum >= BA_ZENBA_STARTNUM
			&& dateNum <= BA_ZENBA_ENDNUM){
			ret = BA_ZENBA;
		}else if(dateNum >= BA_GOBA_STARTNUM
			&& dateNum <= BA_GOBA_ENDNUM){
			ret = BA_GOBA;
		}else if(dateNum >= BA_YUUBA_STARTNUM
			&& dateNum <= 2359){
			ret = BA_YUUBA;
		}
//		}else if(dateNum >= 0
//	            && dateNum <= BA_YUUBA_ENDNUM){
//	            ret = BA_YUUBA;
//	        }
		return ret;
	}

	public static String convertStringSession(short session){
		String ret = "NA";
		if(session == BA_ZENBA){
			ret = "前場";
		}else if(session == BA_GOBA){
			ret = "後場";
		}else if(session == BA_YUUBA){
			ret = "夕場";
		}
		return ret;
	}

	public static short convertShortSession(String session){
		short ret = BA_OTHER;
		if(session.equals("前場")){
			ret = BA_ZENBA;
		}else if(session.equals("後場")){
			ret = BA_GOBA;
		}else if(session.equals("夕場")){
			ret = BA_YUUBA;
		}
		return ret;
	}

	public static String getWeekdayString(int value){
		String ret = "";
		if(value == Calendar.MONDAY){
			ret = "Mon";
		}else if(value == Calendar.TUESDAY){
			ret = "Tue";
		}else if(value == Calendar.WEDNESDAY){
			ret = "Wed";
		}else if(value == Calendar.THURSDAY){
			ret = "Thu";
		}else if(value == Calendar.FRIDAY){
			ret = "Fri";
		}else if(value == Calendar.SATURDAY){
			ret = "Sat";
		}else if(value == Calendar.SUNDAY){
			ret = "Sun";
		}else{
			ret = "error";
		}
		return ret;
	}


	public static int getNum(Date date){
		int dateNum = calendarGet(date, Calendar.HOUR_OF_DAY) * 100
		+ calendarGet(date, Calendar.MINUTE);
		return dateNum;
	}

	public static boolean isMore(Date date, Date time){
		return isMore(date, getNum(time));
	}

	public static boolean isLess(Date date, Date time){
		return isLess(date, getNum(time));
	}

	public static boolean isMore(Date date, int time){
		boolean ret = false;
		int dateNum = calendarGet(date, Calendar.HOUR_OF_DAY) * 100
					+ calendarGet(date, Calendar.MINUTE);
		if(dateNum >= time){
			ret = true;
		}
		return ret;
	}

	public static boolean isLess(Date date, int time){
		boolean ret = false;
		int dateNum = calendarGet(date, Calendar.HOUR_OF_DAY) * 100
					+ calendarGet(date, Calendar.MINUTE);
		if(dateNum <= time){
			ret = true;
		}
		return ret;
	}

	public static boolean isEqual(Date date, int time){
		boolean ret = false;
		int dateNum = calendarGet(date, Calendar.HOUR_OF_DAY) * 100
					+ calendarGet(date, Calendar.MINUTE);
		if(dateNum == time){
			ret = true;
		}
		return ret;
	}

	public static boolean isContained(Date date, int startTime, int endTime){
		boolean ret = false;
		int dateNum = calendarGet(date, Calendar.HOUR_OF_DAY) * 100
					+ calendarGet(date, Calendar.MINUTE);
		if(dateNum >= startTime && dateNum <= endTime){
			ret = true;
		}
		return ret;
	}

	public static boolean isContained(Date date, Date startTime, Date endTime){
		boolean ret = false;
		int dateNum = calendarGet(date, Calendar.HOUR_OF_DAY) * 100
					+ calendarGet(date, Calendar.MINUTE);
		if(dateNum >= getNum(startTime) && dateNum <= getNum(endTime)){
			ret = true;
		}
		return ret;
	}

}
