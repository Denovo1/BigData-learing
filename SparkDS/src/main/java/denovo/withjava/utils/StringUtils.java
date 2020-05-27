package denovo.withjava.utils;


 /**
  * Description：字符串工具类<br/>
  * Copyright (c) ， 2018， Jansonxu <br/>
  * This program is protected by copyright laws. <br/>
  *
  * @author 徐文波
  * @version : 1.0
  */
public class StringUtils {

	/**
	 * 判断字符串是否为空
	 * @param str 字符串
	 * @return 是否为空
	 */
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str);
	}
	
	/**
	 * 判断字符串是否不为空
	 * @param str 字符串
	 * @return 是否不为空
	 */
	public static boolean isNotEmpty(String str) {
		return str != null && !"".equals(str);
	}
	
	/**
	 * 截断字符串两侧的逗号
	 * @param str 字符串
	 * @return 字符串
	 */
	public static String trimComma(String str) {
		int start = 0, end = str.length() - 1;
		while((start <= end) && str.charAt(start) == ',') {
			start++;
		}
		while((start <= end) && str.charAt(end) == ',') {
			end--;
		}
		return str.substring(start, end + 1);
	}
	
	/**
	 * 补全两位数字
	 * @param str
	 * @return
	 */
	public static String fulfuill(String str) {
		if(str.length() == 2) {
			return str;
		} else {
			return "0" + str;
		}
	}
	
	/**
	 * 从拼接的字符串中提取字段
	 * @param str 字符串
	 * @param delimiter 分隔符 
	 * @param field 字段
	 * @return 字段值
	 */
	public static String getFieldFromConcatString(String str, 
			String delimiter, String field) {
		try {
			String[] fields = str.split(delimiter);
			for(String concatField : fields) {
				// searchKeywords=|clickCategoryIds=1,2,3
				if(concatField.split("=").length == 2) {
					String fieldName = concatField.split("=")[0];
					String fieldValue = concatField.split("=")[1];
					if(fieldName.equals(field)) {
						return fieldValue;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 从拼接的字符串中给字段设置值
	 * @param str 字符串
	 * @param delimiter 分隔符 
	 * @param field 字段名
	 * @param newFieldValue 新的field值
	 * @return 字段值
	 */
	public static String setFieldInConcatString(String str, 
			String delimiter, String field, String newFieldValue) {
		String[] fields = str.split(delimiter);
		
		for(int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].split("=")[0];
			if(fieldName.equals(field)) {
				String concatField = fieldName + "=" + newFieldValue;
				fields[i] = concatField;
				break;
			}
		}
		
		StringBuffer buffer = new StringBuffer("");
		for(int i = 0; i < fields.length; i++) {
			buffer.append(fields[i]);
			if(i < fields.length - 1) {
				buffer.append("|");  
			}
		}
		return buffer.toString();
	}

     /**
      * 将传入的字段按顺序和指定分隔符拼接
      * @param separator 分隔符
      * @param strings  要拼接的字符串(可变)
      * @return 拼接后的结果
      */
     public static String appender(String separator, String... strings) {

         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < strings.length; i++) {
             if (i == strings.length - 1) {
                 sb.append(strings[i]);
             } else
                 sb.append(strings[i]).append(separator);
         }
         return sb.toString();
     }

}
