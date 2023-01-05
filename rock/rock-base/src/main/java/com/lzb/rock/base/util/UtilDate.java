package com.lzb.rock.base.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * 日期工具类
 * 
 * @author lzb 2018年2月1日 下午3:18:39
 */
@Component
public class UtilDate {

	/**
	 * 获取当前时间Long值 格式 yyyyMMddHHmmssSSS
	 * 
	 * @return
	 */
	public static Long getFomtTimeByDateLong() {
		String time = getFomtDateByThisTime("yyyyMMddHHmmssSSS");
		return Long.valueOf(time);
	}

	/**
	 * 获取当前时间String值 格式 yyyyMMddHHmmssSSS
	 * 
	 * @return
	 */
	public static String getFomtTimeByDateString() {
		String time = getFomtDateByThisTime("yyyyMMddHHmmssSSS");
		return time;
	}

	/**
	 * 格式 yyyyMMddHHmmssSSS 转换为date
	 * 
	 * @return
	 */
	public static Date getDateByLong(Long date) {
		return getDateByString(date.toString(), "yyyyMMddHHmmssSSS");
	}

	/**
	 * 格式 yyyyMMddHHmmssSSS 转换为date
	 * 
	 * @return
	 */
	public static Date getDateByString(String date) {
		return getDateByString(date, "yyyyMMddHHmmssSSS");
	}

	/**
	 * 获取当前时间Long值 标准时间戳
	 * 
	 * @return
	 */
	public static Long getDateTimeByDate() {
		return getDateTimeByDate(new Date());
	}

	/**
	 * 获取指定时间long值 标准时间戳
	 * 
	 * @param date
	 * @return
	 */
	public static Long getDateTimeByDate(Date date) {
		return date.getTime();
	}

	/**
	 * 获取标准时间戳转换为int
	 * 
	 * @return
	 */
	public static Integer getTimeInteger() {
		return getDateTimeByDate().intValue();
	}

	/**
	 * 获取指定Long值 指定格式 时间戳
	 * 
	 * @return
	 */
	public static Long getDateTimeByFomtDate(String dateStr, String fomt) {
		Date date = getDateByString(dateStr, fomt);
		return getDateTimeByDate(date);
	}

	/**
	 * 标准时间戳long值转换为date类型
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateByDateTime(Long date) {
		return new Date(date);
	}

	/**
	 * 指定格式字符串转date类型
	 * 
	 * @param fomt
	 * @param dateStr
	 * @return
	 */
	public static Date getDateByString(String dateStr, String fomt) {
		SimpleDateFormat sdf = new SimpleDateFormat(fomt);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取当前时间转换指定格式
	 * 
	 * @param fomt
	 * @return
	 */
	public static String getFomtDateByThisTime(String fomt) {
		SimpleDateFormat sdf = new SimpleDateFormat(fomt);
		return sdf.format(new Date());
	}

	/**
	 * 格式化指定时间
	 * 
	 * @param fomt
	 * @return
	 */
	public static String getFomtDateByDate(String fomt, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(fomt);
		return sdf.format(date);
	}

	/**
	 * 获取系统当前时间戳(10位到秒)
	 *
	 * @return int
	 */
	public static long time() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 获得今天凌晨时间戳
	 * 
	 * @return int
	 */
	public static int dayBreakTime() {
		/**
		 * 获取今天日期时间
		 */
		String nowTime = getFomtDateByThisTime("yyyy-MM-dd") + " 00:00:00";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		Date date = null;
		try {
			date = sdf.parse(nowTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 * 转化时间戳
		 */
		return (int) (date.getTime() / 1000);
	}

	/**
	 * 获得今天凌晨时间
	 * 
	 * @return int
	 */
	public static Date dayStart() {
		/**
		 * 获取今天日期时间
		 */
		String nowTime = getFomtDateByThisTime("yyyy-MM-dd") + " 00:00:00";
		return getDateByString(nowTime, "yyyy-MM-dd hh:mm:ss");
	}

	/**
	 * 获得今天最后时间
	 * 
	 * @return int
	 */
	public static Date dayEnd() {
		/**
		 * 获取今天日期时间
		 */
		String nowTime = getFomtDateByThisTime("yyyy-MM-dd") + " 23:59:59";

		return getDateByString(nowTime, "yyyy-MM-dd hh:mm:ss");
	}

	/**
	 * 获取1970-00-00 00:00:00的date
	 * 
	 * @return
	 */
	public static Date getStartDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse("1970-00-00 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 指定日期加上天数后的日期
	 * 
	 * @param date 指定日期
	 * @param num  为增加的天数
	 * @return
	 * @throws ParseException
	 */
	public static Date addDayDate(Date date, Integer num) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		/**
		 * num为增加的天数，可以改变的
		 */
		ca.add(Calendar.DATE, num);
		return ca.getTime();
	}

	// 当前日期加上天数：

	/**
	 * 当前日期加上天数后的日期
	 * 
	 * @param num 为增加的天数
	 * @return
	 */
	public static Date addDay(Integer num) {

		return addDayDate(new Date(), num);
	}

	/**
	 * 在原日期的基础上增加小时数
	 * 
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date addHourOfDate(Date date, Integer i) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(Calendar.HOUR_OF_DAY, i);
		return ca.getTime();
	}

	/**
	 * 在原日期的基础上增加分钟
	 * 
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date addMinuteOfDate(Date date, Integer i) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(Calendar.MINUTE, i);
		return ca.getTime();
	}

	/**
	 * 根据年月日计算年龄,birthTimeString:"1994-11-14" yyyy-MM-dd
	 * 
	 * @param birthTimeString
	 * @return
	 */
	public static Integer getAgeFromBirthTime(String birthTimeString) {
		// 先截取到字符串中的年、月、日
		String[] strs = birthTimeString.trim().split("-");
		Integer selectYear = Integer.parseInt(strs[0]);
		Integer selectMonth = Integer.parseInt(strs[1]);
		Integer selectDay = Integer.parseInt(strs[2]);
		// 得到当前时间的年、月、日
		Calendar cal = Calendar.getInstance();
		Integer yearNow = cal.get(Calendar.YEAR);
		Integer monthNow = cal.get(Calendar.MONTH) + 1;
		Integer dayNow = cal.get(Calendar.DATE);

		// 用当前年月日减去生日年月日
		Integer yearMinus = yearNow - selectYear;
		Integer monthMinus = monthNow - selectMonth;
		Integer dayMinus = dayNow - selectDay;
		/**
		 * 先大致赋值
		 */
		Integer age = yearMinus;
		// 选了未来的年份
		if (yearMinus < 0) {
			age = 0;
			/**
			 * 同年的，要么为1，要么为0
			 */
		} else if (yearMinus == 0) {
			/**
			 * 选了未来的月份
			 */
			if (monthMinus < 0) {
				age = 0;
				/**
				 * 同月份的
				 */
			} else if (monthMinus == 0) {
				/**
				 * 选了未来的日期
				 */
				if (dayMinus < 0) {
					age = 0;
				} else if (dayMinus >= 0) {
					age = 1;
				}
			} else if (monthMinus > 0) {
				age = 1;
			}
		} else if (yearMinus > 0) {
			/**
			 * 当前月>生日月
			 */
			if (monthMinus < 0) {
				/**
				 * 同月份的，再根据日期计算年龄
				 */
			} else if (monthMinus == 0) {
				if (dayMinus < 0) {
				} else if (dayMinus >= 0) {
					age = age + 1;
				}
			} else if (monthMinus > 0) {
				age = age + 1;
			}
		}
		return age;
	}

	/**
	 * 根据时间戳（10位）计算年龄
	 * 
	 * @param birthTimeLong
	 * @return
	 */
	public static int getAgeFromBirthTime(long birthTimeLong) {
		String fomt = "yyyy-MM-dd";
		Date date = new Date(birthTimeLong * 1000L);
		SimpleDateFormat format = new SimpleDateFormat(fomt);
		String dateStr = format.format(date);
		return getAgeFromBirthTime(dateStr);
	}

	/**
	 * 获取当前月第一天：
	 * 
	 * @return
	 */
	public static Date getMonthFirseDate() {
		String fomt = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(fomt);
		// 获取前月的第一天
		Calendar cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 0);
		cale.set(Calendar.DAY_OF_MONTH, 1);
		String dateStr = format.format(cale.getTime());
		return getDateByString(dateStr, fomt);

	}

	/**
	 * 获取当前月最后一天
	 * 
	 * @return
	 */
	public static Date getMonthLastDate() {
		String fomt = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(fomt);
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		String last = format.format(ca.getTime());
		return getDateByString(last, fomt);
	}

	/**
	 * 
	 * 描述:获取下一个月的第一天.
	 * 
	 * @return
	 */
	public static String getPerFirstDayOfMonth() {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return dft.format(calendar.getTime());
	}

	/**
	 * 判断两个时间是否超过一天
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean overOneDay(Date startTime, Date endTime) {
		long between = endTime.getTime() - startTime.getTime();
		if (between > (24 * 3600000)) {
			return true;
		}
		return false;
	}

	/**
	 * solr日期格式化
	 * 
	 * @param date
	 * @return
	 */
	public static String solrFormat(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fmt = sdf.format(date);
		fmt = fmt.replaceAll(" ", "T");
		fmt = fmt + "Z";
		return fmt;

	}

	/**
	 * 毫秒转换成 xx小时 xx 分钟 xx 秒
	 * 
	 * @date 2020年9月21日下午3:08:56
	 * @param time 毫秒
	 * @return
	 */
	public static String getTimeText(Integer time) {

		StringBuffer timeText = new StringBuffer();

		if (time > 0 && time / (1000 * 60 * 60 * 24) > 1) {
			Integer num = (int) Math.floor(time / (1000 * 60 * 60 * 24));
			timeText.append(" ").append(num).append(" 天");
			time = time - 1000 * 60 * 60 * 24 * num;
		}

		if (time > 0 && time / (1000 * 60 * 60) > 1) {
			Integer num = (int) Math.floor(time / (1000 * 60 * 60));
			timeText.append(" ").append(num).append(" 小时");
			time = time - 1000 * 60 * 60 * num;
		}

		if (time > 0 && time / (1000 * 60) > 1) {
			Integer num = (int) Math.floor(time / (1000 * 60));
			timeText.append(" ").append(num).append(" 分钟");
			time = time - 1000 * 60 * num;
		}

		if (time > 0 && time / 1000 > 1) {
			Integer num = (int) Math.floor(time / 1000);

			timeText.append(num).append(" 秒");
			time = time - 1000 * num;
		}

		return timeText.toString();

	}
}
