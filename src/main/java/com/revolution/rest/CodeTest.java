package com.revolution.rest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.revolution.rest.common.HttpUtil;
import com.revolution.rest.dao.NotificationDao;
import com.revolution.rest.dao.PushNotificationDao;
import com.revolution.rest.model.PushNotification;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CodeTest {
	char[] table = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	static {
		System.out.println("Word static initialization!");
		;
	}

	public String getShortenedUrl(String url) {
		return convert_10base_to_62base(123);
	}

	public String convert_10base_to_62base(long n) {
		StringBuilder sb = new StringBuilder();
		n = n + 39777216l;
		while (n > 0) {
			sb.insert(0, table[(int) n % 16]);
			n = n / 16;
		}
		for (int i = 0; i < 5; i++) {

		}
		System.out.println(sb);
		return sb.toString();
	}

	public int getIdFromDB() {
		Random r = new Random();
		return r.nextInt(1000000);
	}

	public void rand() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 5; i++) {
			int count = (int) (Math.random() * 16);
			sb.append(table[count]);
		}
		System.out.println(sb.toString());

	}

	public String makeURL(long n) {
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < 5; i++) {
			int count = (int) (Math.random() * 16);
			sb1.append(table[count]);
		}
		StringBuilder sb2 = new StringBuilder();
		n = n + 18977216l;
		while (n > 0) {
			sb2.insert(0, table[(int) n % 16]);
			n = n / 16;
		}
		String id = sb2.toString();
		String subId = id.substring(0, 5);
		int a = Integer.parseInt(subId, 16) + Integer.parseInt(sb1.toString(), 16);
		String s = Integer.toHexString(a);
		String url = sb1.toString() + s + id.substring(5, 7);
		return url;
	}

	public static String encryptSHA(String decript) throws Exception {

		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("SHA");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";

	}

	public static String lastWeek() {
		Date date = new Date();
		int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
		int month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
		int day = Integer.parseInt(new SimpleDateFormat("dd").format(date)) - 3;

		if (day < 1) {
			month -= 1;
			if (month == 0) {
				year -= 1;
				month = 12;
			}
			if (month == 4 || month == 6 || month == 9 || month == 11) {
				day = 30 + day;
			} else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10
					|| month == 12) {
				day = 31 + day;
			} else if (month == 2) {
				if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
					day = 29 + day;
				else
					day = 28 + day;
			}
		}
		String y = year + "";
		String m = "";
		String d = "";
		if (month < 10)
			m = "0" + month;
		else
			m = month + "";
		if (day < 10)
			d = "0" + day;
		else
			d = day + "";

		return y + "-" + m + "-" + d;
	}

	static String url = "http://sdk.open.api.igexin.com/apiex.htm";

	public static NotificationTemplate notificationTemplateDemo(String appId, String appKey, String masterSecret,
			String content) throws Exception {
		NotificationTemplate template = new NotificationTemplate();
		// 设置APPID与APPKEY
		template.setAppId(appId);
		template.setAppkey(appKey);
		// 设置通知栏标题与内容
		template.setTitle("请输入通知栏标题");
		template.setText("请输入通知栏内容");
		// 配置通知栏图标
		template.setLogo("icon.png");
		// 配置通知栏网络图标
		template.setLogoUrl("");
		// 设置通知是否响铃，震动，或者可清除
		template.setIsRing(true);
		template.setIsVibrate(true);
		template.setIsClearable(true);
		// 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		template.setTransmissionType(2);
		template.setTransmissionContent(content);
		return template;
	}

	public static void pushInfoAll(String appId, String appKey, String masterSecret, String content) throws Exception {
		IGtPush push = new IGtPush(url, appKey, masterSecret);
		push.connect();
		NotificationTemplate template = notificationTemplateDemo(appId, appKey, masterSecret, content);

		SingleMessage message = new SingleMessage();
		message.setOffline(true);

		message.setOfflineExpireTime(86400000L);
		message.setData(template);

		Target target = new Target();
		target.setAppId(appId);
		target.setClientId("e0374fee73dcff9ffc54f77d9512fee0");
		IPushResult ret = push.pushMessageToSingle(message, target);
		System.out.println(ret.getResponse().toString());
	}

	public static String loadJson(String url) {
		StringBuilder json = new StringBuilder();
		try {
			URL urlObject = new URL(url);
			URLConnection uc = urlObject.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				json.append(inputLine);
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public static void push() throws Exception {
		String path = loadJson("file:\\E:\\server-backend\\tella-webservice\\WebContent\\META-INF\\getui.json");
		JSONObject json1 = JSONObject.fromObject(path);
		String appId = json1.getString("appId");
		String appKey = json1.getString("appKey");
		String masterSecret = json1.getString("masterSecret");
		/*
		 * String content = "壹頁更新至1.0正式版，此版本为内测最后一个版本。详情请点击壹頁APP";
		 * pushInfoAll(appId,appKey,masterSecret,content);
		 */
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"file:\\E:\\server-backend\\tella-webservice\\WebContent\\WEB-INF\\applicationContext.xml");
		// ApplicationContext context=new
		// FileSystemXmlApplicationContext("E:\\workspace\\tella-webservice\\WebContent\\WEB-INF\\applicationContext.xml");
		PushNotificationDao pushNotificationDao = (PushNotificationDao) context.getBean("pushNotificationDao");
		NotificationDao notificationDao = (NotificationDao) context.getBean("notificationDao");
		List<PushNotification> pnList = pushNotificationDao.getPushNotificationByUserid(107l);
		Map<String, Integer> map = new HashMap<String, Integer>();
		if ((pnList != null) && (pnList.size() > 0)) {
			for (PushNotification pn : pnList) {
				int count = notificationDao.getNotificationByRecipientId(pn.getUserId());
				map.put(pn.getClientId(), Integer.valueOf(count));
				System.out.println(pn.getClientId());
			}
		}
		String content = "壹頁更新至1.2正式版，详情请点击壹頁APP";
		// PushNotificationUtil.pushInfoAllFollow(appId, appKey, masterSecret,
		// pnList, map, content);
		System.out.println("success");
	}

	public static String sendGet(String url) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	public void timer1() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				System.out.println("-------设定要指定任务--------");
			}
		}, 2);// 设定指定的时间time,此处为2000毫秒
	}

	/**
	 * 
	 * @param 短信通客户接口测试
	 * @param sendsmsaddress
	 * @return
	 * 
	 */
	public String commandID = "3";
	public String username = "rujiastore";
	public String password = "rujiastore5689";
	public String serviceURL = "http://124.173.70.59:8081/SmsAndMms/mt";

	public static String connectURL(String commString, String sendsmsaddress) {
		String rec_string = "";
		URL url = null;
		HttpURLConnection urlConn = null;
		try {
			url = new URL(sendsmsaddress); // 根据数据的发送地址构建URL
			urlConn = (HttpURLConnection) url.openConnection(); // 打开链接
			urlConn.setConnectTimeout(30000); // 链接超时设置为30秒
			urlConn.setReadTimeout(30000); // 读取超时设置30秒
			urlConn.setRequestMethod("POST"); // 链接相应方式为post
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);

			OutputStream out = urlConn.getOutputStream();
			out.write(commString.getBytes("UTF-8"));
			out.flush();
			out.close();

			BufferedReader rd = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			int ch;
			while ((ch = rd.read()) > -1) {
				sb.append((char) ch);
			}

			rec_string = sb.toString().trim();
			rec_string = URLDecoder.decode(rec_string, "UTF-8");
			rd.close();
		} catch (Exception e) {
			rec_string = "-107";
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}

		return rec_string;
	}

	public String sendSms(String mobile, String content) {
		String res = "";
		try {
			String commString = "Sn=" + username + "&Pwd=" + password + "&mobile=" + mobile + "&content=" + content;
			res = connectURL(commString, serviceURL);
		} catch (Exception e) {
			return "-10000";
		}
		// 设置返回值 解析返回值
		String resultok = "";
		// //正则表达式
		Pattern pattern = Pattern.compile("<int xmlns=\"http://tempuri.org/\">(.*)</int>");
		Matcher matcher = pattern.matcher(res);
		while (matcher.find()) {
			resultok = matcher.group(1);
		}
		return resultok;
	}

	public static Date date = null;

	static {
		date = new Date();
	}

	/**
	 * 从文件中加载私钥
	 * 
	 * @param keyFileName
	 *            私钥文件名
	 * @return 是否成功
	 * @throws Exception
	 */
	public static String loadPrivateKeyByFile(String path) throws Exception {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(path + "/app/tomcat/webapps/tella-webservice/META-INF/database.properties"));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		}
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String ascii2native(String ascii) {
		int n = ascii.length() / 6;
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0, j = 2; i < n; i++, j += 6) {
			String code = ascii.substring(j, j + 4);
			char ch = (char) Integer.parseInt(code, 16);
			sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * unicode 转字符串
	 */
	public static String unicode2String(String unicode) {
		StringBuffer string = new StringBuffer();
		System.out.println("unicode-->" + unicode);
		String[] hex = unicode.split("\\\\u");

		for (int i = 1; i < hex.length; i++) {
			System.out.println(hex[i]);
			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);

			// 追加成string
			string.append((char) data);
		}

		return string.toString();
	}

	public static String getWeekOfDate(Date dt) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		System.out.println(w);
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	// 判断是否为数字
	public static boolean isNumber(String str) {
		try {
			int number = Integer.parseInt(str);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	// 如果字符串是数字，就返回本身，如果是混合的，就返回数字的值。
	public static double String2Double(String str) {
		if (isNumber(str)) {
			return Double.parseDouble(str);
		} else {
			char[] charArr = str.toCharArray();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < charArr.length; i++) {
				if (isNumber(new String(charArr[i] + ""))) {
					sb.append(charArr[i]);
				} else {
					if ((charArr[i] + "").equalsIgnoreCase(".")) {
						sb.append(charArr[i]);
					}
					continue;
				}
			}

			return Double.parseDouble(sb.toString());
		}

	}

	public static String t1(String input) {
		String reg = "[a-zA-Z]";
		StringBuffer strBuf = new StringBuffer();
		input = input.toLowerCase();
		if (null != input && !"".equals(input)) {
			for (char c : input.toCharArray()) {
				if (String.valueOf(c).matches(reg)) {
					strBuf.append(c - 96);
				} else {
					strBuf.append(c);
				}
			}
			return strBuf.toString();
		} else {
			return input;
		}
	}

	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	// ------------------------------------------------------------

	/**
	 * 获取网页的内容
	 */
	private static Document getURLContent(String url) throws Exception {
		Document doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").cookie("auth", "token")
				.timeout(6000).post();
		return doc;
	}

	/**
	 * 获取网页的内容
	 */
	private static Document getURLContentGET(String url) throws Exception {
		Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
		return doc;
	}

	/**
	 * 获取优酷视频
	 * 
	 * @param url
	 *            视频URL
	 */
	public static void getYouKuVideo(String url) throws Exception {
		Document doc = getURLContent(url);
		Element content = doc.getElementById("link4");
		System.out.println(content.val());
		/**
		 * 获取视频缩略图
		 */
		String pic = getElementAttrById(doc, "s_sina", "href");
		int local = pic.indexOf("pic=");
		pic = pic.substring(local + 4);

		/**
		 * 获取视频地址
		 */
		String flash = getElementAttrById(doc, "link2", "value");

		/**
		 * 获取视频时间
		 */
		String time = getElementAttrById(doc, "download", "href");
		String[] arrays = time.split("\\|");
		time = arrays[4];
		System.out.println("pic-->" + pic + ",flash-->" + flash + ",-->" + time);
	}

	// qq
	public static void getqqVideo(String url) throws Exception {
		Document doc = getURLContent(url);
		// Element content = doc.getElementById("link4");
		System.out.println(doc.toString());

	}

	/**
	 * 获取script某个变量的值
	 * 
	 * @param name
	 *            变量名称
	 * @return 返回获取的值
	 */
	private static String getScriptVarByName(String name, String content) {
		String script = content;

		int begin = script.indexOf(name);

		script = script.substring(begin + name.length() + 2);

		int end = script.indexOf(",");

		script = script.substring(0, end);

		String result = script.replaceAll("'", "");
		result = result.trim();

		return result;
	}

	/**
	 * 根据HTML的ID键及属于名，获取属于值
	 * 
	 * @param id
	 *            HTML的ID键
	 * @param attrName
	 *            属于名
	 * @return 返回属性值
	 */
	private static String getElementAttrById(Document doc, String id, String attrName) throws Exception {
		Element et = doc.getElementById(id);
		String attrValue = et.attr(attrName);

		return attrValue;
	}

	// ---------------------------------------------------------

	public static void main(String[] args) throws Exception {
		String s = "这是一段中文字符串"; 
		byte[] b = s.getBytes("UTF-8"); 
		
		String n = new String(b,"UTF-8");
		System.out.println(b.toString());
	}
	
	/**
	 * 
	 * 
	 * @param input
	 */

	public static void letterToNum(String input) {
		for (byte b : input.getBytes()) {
			System.out.print(b - 96);
		}
	}

	public static int t2(String s) {// 字符串转换为ASCII码
		char[] chars = s.toCharArray(); // 把字符中转换为字符数组
		int num = 0;
		for (int i = 0; i < chars.length; i++) {// 输出结果
			num += (int) chars[i];
		}
		return num;
	}

}
