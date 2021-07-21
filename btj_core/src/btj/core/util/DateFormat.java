package btj.core.util;

import java.text.SimpleDateFormat;

public class DateFormat {
	public static final String DF_YMDHMS_DEFAULT =
		"0000/00/00 00:00:00";
	public static final SimpleDateFormat DF_YMDHMSS_SL
	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
	public static final SimpleDateFormat DF_YMDHMS_SL
		= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static final SimpleDateFormat DF_YMDHMS_PE
		= new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	public static final SimpleDateFormat DF_YMDHMS_FT
		= new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat DF_YMD_SL
		= new SimpleDateFormat("yyyy/MM/dd");
	public static final SimpleDateFormat DF_YMD_PE
		= new SimpleDateFormat("yyyy.MM.dd");
	public static final SimpleDateFormat DF_YMD_NO
		= new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat DF_YM_NO
    = new SimpleDateFormat("yyyyMM");

	/**
	 * yyyy-MM-dd
	 */
	public static final SimpleDateFormat DF_YMD_HH
		= new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat DF_Y
	= new SimpleDateFormat("yyyy");

	   public static final SimpleDateFormat DF_YMDHM_HH
       = new SimpleDateFormat("yyyy-MM-dd HH:mm");


	public static final SimpleDateFormat DF_YMDHMS_NO
		= new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat DF_HM_CL
		= new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat DF_HM_NO
		= new SimpleDateFormat("HHmm");
	public static final SimpleDateFormat DF_HMS_CL
	= new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat DF_HMSS_CL
		= new SimpleDateFormat("HH:mm:ss:SSS");

	public static final SimpleDateFormat DF_TGS
		= new SimpleDateFormat("yyyyMMdd HH:mm:ss:SSS");
}
