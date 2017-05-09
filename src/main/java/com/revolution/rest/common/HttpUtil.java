package com.revolution.rest.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.revolution.rest.model.SdkHttpResult;

import net.sf.json.JSONObject;

public class HttpUtil {

	private static final String APPKEY = "RC-App-Key";
	private static final String NONCE = "RC-Nonce";
	private static final String TIMESTAMP = "RC-Timestamp";
	private static final String SIGNATURE = "RC-Signature";
	private static final int[] privateKeyIndex = { 0, 1, 3, 4, 5, 7, 9 };
	private static final String[] privateKey = { "snc", "pa", "fbfe", "am", "sk", "jugg", "fow", "spe", "en", "sf" };
	private static final String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO6jyH";

	// 设置body体
	public static void setBodyParameter(StringBuilder sb, HttpURLConnection conn) throws IOException {
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(sb.toString());
		out.flush();
		out.close();
	}

	// 添加签名header
	public static HttpURLConnection CreatePostHttpConnection(String appKey, String appSecret, String uri)
			throws MalformedURLException, IOException, ProtocolException {
		String nonce = String.valueOf(Math.random() * 1000000);
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		StringBuilder toSign = new StringBuilder(appSecret).append(nonce).append(timestamp);
		String sign = CodeUtil.hexSHA1(toSign.toString());

		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setInstanceFollowRedirects(true);
		conn.setConnectTimeout(30000);
		conn.setReadTimeout(30000);

		conn.setRequestProperty(APPKEY, appKey);
		conn.setRequestProperty(NONCE, nonce);
		conn.setRequestProperty(TIMESTAMP, timestamp);
		conn.setRequestProperty(SIGNATURE, sign);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		return conn;
	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}

	public static SdkHttpResult returnResult(HttpURLConnection conn) throws Exception, IOException {
		String result;
		InputStream input = null;
		if (conn.getResponseCode() == 200) {
			input = conn.getInputStream();
		} else {
			input = conn.getErrorStream();
		}
		result = new String(readInputStream(input), "UTF-8");
		return new SdkHttpResult(conn.getResponseCode(), result);
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
	public static String sendPost(String url, JSONObject json) {
		BufferedReader in = null;
		DataOutputStream out = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setRequestMethod("POST");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
			conn.connect();
			// 获取URLConnection对象对应的输出流
			out = new DataOutputStream(conn.getOutputStream());
			// 发送请求参数
			out.writeBytes(json.toString());
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			conn.disconnect();
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

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPostStr(String url, String json) {
		BufferedReader in = null;
		DataOutputStream out = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			// conn.setRequestProperty("Content-Type","multipart/form-data");
			conn.setRequestMethod("POST");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new DataOutputStream(conn.getOutputStream());
			// 发送请求参数
			out.write(json.getBytes("utf-8"));
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
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
	

	// HTTP GET request
	public static String sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}

	/*
	 * public static String splitJSON(JSONObject json) { JSONObject param =
	 * json; Iterator<String> parentIter = param.keys(); StringBuffer parentSB =
	 * new StringBuffer(); StringBuffer systemSB = new StringBuffer();
	 * StringBuffer clientInfoSB = new StringBuffer(); while
	 * (parentIter.hasNext()) { String key = parentIter.next();
	 * System.out.println(key); if (key.equals("systemParameterInfo")) {
	 * JSONObject systemJson = param.getJSONObject(key); Iterator<String>
	 * systemIter = systemJson.keys();
	 * 
	 * while (systemIter.hasNext()) { String sysKey = systemIter.next();
	 * 
	 * if (sysKey.equals("clientInfo")) {
	 * 
	 * JSONObject clientInfoJSON = systemJson.getJSONObject(sysKey);
	 * Iterator<String> clientKey = clientInfoJSON.keys(); while
	 * (clientKey.hasNext()) { String cKey = clientKey.next();
	 * clientInfoSB.append(cKey + "=" + clientInfoJSON.getString(cKey) + "&"); }
	 * String clientInfoStr = clientInfoSB.toString(); clientInfoStr =
	 * clientInfoStr.substring(0, clientInfoStr.length() - 1);
	 * systemSB.append(sysKey + "=" + clientInfoStr + "&"); } else {
	 * systemSB.append(sysKey + "=" + systemJson.getString(sysKey) + "&"); }
	 * 
	 * } String systemStr = systemSB.toString(); parentSB.append(key + "=" +
	 * systemStr.substring(0, systemStr.length() - 1) + "&"); } else {
	 * parentSB.append(key + "=" + param.getString(key) + "&"); } } String
	 * parentStr = parentSB.toString(); parentStr = parentStr.substring(0,
	 * parentStr.length() - 1); return parentStr;
	 * 
	 * }
	 */

	public static String splitJSON(JSONObject param) {
		StringBuffer paramSB = new StringBuffer();
		StringBuffer clientInfoSB = new StringBuffer();
		StringBuffer systemSB = new StringBuffer();
		JSONObject systemJson = JSONObject.fromObject(param.get("systemParameterInfo"));
		JSONObject clientInfo = JSONObject.fromObject(systemJson.get("clientInfo"));
		Iterator<String> kIte = clientInfo.keys();
		Map<String, Integer> map = new HashMap<String, Integer>();
		while (kIte.hasNext()) {
			String key = kIte.next();
			String val = clientInfo.getString(key);
			String one = val.substring(0, 1);

			if (isNumeric(one)) {
				map.put(key, Integer.parseInt(one));
			} else {
				int aa = tranASCII(one);
				map.put(key, aa);
			}
		}
		List<Map.Entry<String, Integer>> sortList = sortMap(map);
		for (Entry<String, Integer> e : sortList) {
			String ciKey = e.getKey();
			clientInfoSB.append(ciKey + "=" + clientInfo.getString(ciKey) + "&");
		}
		String clientInfoStr = clientInfoSB.toString();
		clientInfoStr = clientInfoStr.substring(0, clientInfoStr.length() - 1);

		// systemParameterInfo
		systemJson.put("clientInfo", clientInfoStr);

		Iterator<String> sIte = systemJson.keys();
		Map<String, Integer> sysMap = new HashMap<String, Integer>();
		while (sIte.hasNext()) {
			String key = sIte.next();
			String val = systemJson.getString(key);
			String one = val.substring(0, 1);

			if (isNumeric(one)) {
				sysMap.put(key, Integer.parseInt(one));
			} else {
				int aa = tranASCII(one);
				sysMap.put(key, aa);
			}
		}

		List<Map.Entry<String, Integer>> sysSortList = sortMap(sysMap);
		for (Entry<String, Integer> sys : sysSortList) {
			String sysKey = sys.getKey();
			systemSB.append(sysKey + "=" + systemJson.getString(sysKey) + "&");
		}
		String systemStr = systemSB.toString();
		systemStr = systemStr.substring(0, systemStr.length() - 1);
		// ---------param
		param.put("systemParameterInfo", systemStr);
		Iterator<String> pIte = param.keys();
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		while (pIte.hasNext()) {
			String key = pIte.next();
			String val = param.getString(key);
			String one = val.substring(0, 1);

			if (isNumeric(one)) {
				paramMap.put(key, Integer.parseInt(one));
			} else {
				int aa = tranASCII(one);
				paramMap.put(key, aa);
			}
		}

		List<Map.Entry<String, Integer>> paramSortList = sortMap(paramMap);
		for (Entry<String, Integer> par : paramSortList) {
			String parKey = par.getKey();
			paramSB.append(parKey + "=" + param.getString(parKey) + "&");
		}
		String parentStr = paramSB.toString();
		parentStr = parentStr.substring(0, parentStr.length() - 1);
		return parentStr;

	}
	
	public static String splitJSONCopy(JSONObject param) {
		StringBuffer paramSB = new StringBuffer();
		Iterator<String> pIte = param.keys();
		Map<String, String> paramMap = new HashMap<String, String>();
		while (pIte.hasNext()) {
			String key = pIte.next();
			String val = param.getString(key);
//			String one = val.substring(0, 1);

			paramMap.put(key, val);
		}

		List<Map.Entry<String, String>> paramSortList = sortMapCopy(paramMap);
		for (Entry<String, String> par : paramSortList) {
			String parKey = par.getKey();
			paramSB.append(parKey + "=" + param.getString(parKey) + "&");
		}
		String parentStr = paramSB.toString();
		parentStr = parentStr.substring(0, parentStr.length() - 1);
		return parentStr;

	}

	public static String sign(String tag_info) {
		String result = "";
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(tag_info.getBytes());
			int i;
			byte b[] = md.digest();
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
			return result;
		} catch (Exception e) {
			return result;
		}
	}

	public static String getPrivateKey(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return privateKey[privateKeyIndex[w]];

	}

	// 得到32位的MD5加密字符串
	public static String getMD5Str(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest messageDigest = null;

		messageDigest = MessageDigest.getInstance("MD5");

		messageDigest.reset();

		messageDigest.update(str.getBytes("UTF-8"));

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	// 按value进行排序 如果value相同 则按key进行排序

	public static List<Map.Entry<String, Integer>> sortMap(Map<String, Integer> map) {
		List<Map.Entry<String, Integer>> mappingList = null;
		mappingList = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		Collections.sort(mappingList, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				int i = o1.getValue().compareTo(o2.getValue());

				if (i == 0) {
					Integer k1 = tranASCII(o1.getKey().substring(0, 1));
					Integer k2 = tranASCII(o2.getKey().substring(0, 1));
					i = k1.compareTo(k2);
				}
				return i;
			}
		});

		return mappingList;
	}
	
	// 按value进行排序 如果value相同 则按key进行排序
	public static List<Map.Entry<String, String>> sortMapCopy(Map<String, String> map) {
		List<Map.Entry<String, String>> mappingList = null;
		mappingList = new ArrayList<Map.Entry<String, String>>(map.entrySet());
		Collections.sort(mappingList, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Entry<String, String> o1, Entry<String, String> o2) {
				int j = o1.getValue().compareTo(o2.getValue());
				if(o1.getValue().length() == o2.getValue().length()){
					int len = o1.getValue().length();
					for(int i=0;i<len;i++){
						char c1 = o1.getValue().charAt(i);
						char c2 = o2.getValue().charAt(i);
						if(c1 > c2){
							j=1;
							break;
						}else if(c1 < c2){
							j=-1;
							break;
						}
					}
				}
				if (j == 0) {
					Integer k1 = tranASCII(o1.getKey().substring(0, 1));
					Integer k2 = tranASCII(o2.getKey().substring(0, 1));
					j = k1.compareTo(k2);
				}
				return j;
			}
		});

		return mappingList;
	}

	// 转换成ASCII码
	public static int tranASCII(String s) {// 字符串转换为ASCII码
		char[] chars = s.toCharArray(); // 把字符中转换为字符数组
		int num = 0;
		for (int i = 0; i < chars.length; i++) {// 输出结果
			num += (int) chars[i];
		}
		return num;
	}

	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取网页的内容
	 */
	private static Document getURLContent(String url) throws Exception {
		Document doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").cookie("auth", "token")
				.timeout(6000).post();
		return doc;
	}

	/**
	 * 获取优酷视频
	 * 
	 * @param url
	 *            视频URL
	 */
	public static String getYouKuVideo(String url) throws Exception {
		Document doc = getURLContent(url);
		Element content = doc.getElementById("link4");
		if (content != null) {
			return content.val();
		} else {
			return null;
		}
		/**
		 * 获取视频缩略图
		 */
		/*
		 * String pic = getElementAttrById(doc, "s_sina", "href"); int local =
		 * pic.indexOf("pic="); pic = pic.substring(local+4);
		 * 
		 *//**
			 * 获取视频地址
			 */
		/*
		 * String flash = getElementAttrById(doc, "link2", "value");
		 * 
		 *//**
			 * 获取视频时间
			 *//*
			 * String time = getElementAttrById(doc, "download", "href"); String
			 * []arrays = time.split("\\|"); time = arrays[4];
			 * System.out.println("pic-->"+pic+",flash-->"+flash+",-->"+time);
			 */
	}
}
