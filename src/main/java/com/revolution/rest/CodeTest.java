package com.revolution.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.revolution.rest.dao.NotificationDao;
import com.revolution.rest.dao.PushNotificationDao;
import com.revolution.rest.model.PushNotification;

import net.sf.json.JSONObject;


public class CodeTest {
		 char[] table = new char[] {
	        '0','1','2','3','4','5','6','7','8','9',
	        'a','b','c','d','e','f'
	    };
		 
		 static{  
		        System.out.println("Word static initialization!");;  
		    } 

	    public String getShortenedUrl(String url) {
	        return convert_10base_to_62base(123);
	    }

	    public String convert_10base_to_62base(long n) {
	        StringBuilder sb = new StringBuilder();
	        n = n+39777216l;
	        while (n>0) {
	            sb.insert(0,table[(int)n%16]);
	            n = n/16;
	        }
	        for(int i=0;i<5;i++){
	        	
	        }
	        System.out.println(sb);
	        return sb.toString();
	    }

	    public int getIdFromDB() {
	        Random r = new Random();
	        return r.nextInt(1000000);
	    }
	    
	    public void rand(){
	    	StringBuffer sb = new StringBuffer(); 
	    	for(int i=0;i<5;i++){
	    		int count = (int)(Math.random()*16);
	    		sb.append(table[count]);
	    	}
	    	System.out.println(sb.toString());
	    	
	    }
	    
	    public String makeURL(long n) {
	 	   StringBuffer sb1 = new StringBuffer();
	    	for(int i=0;i<5;i++){
	    		int count = (int)(Math.random()*16);
	    		sb1.append(table[count]);
	    	}
	        StringBuilder sb2 = new StringBuilder();
	        n = n+18977216l;
	        while (n>0) {
	            sb2.insert(0,table[(int)n%16]);
	            n = n/16;
	        }
	        String id = sb2.toString();
	        String subId  = id.substring(0, 5);
	        int a = Integer.parseInt(subId,16)+Integer.parseInt(sb1.toString(), 16);
	        String s = Integer.toHexString(a);
	        String url = sb1.toString() + s + id.substring(5,7);
	        return url;
	    }
	    
	    public static String encryptSHA(String decript) throws Exception {

			try {
				MessageDigest digest = java.security.MessageDigest
						.getInstance("SHA");
				digest.update(decript.getBytes());
				byte messageDigest[] = digest.digest();
				// Create Hex String
				StringBuffer hexString = new StringBuffer();
				// �ֽ�����ת��Ϊ ʮ������ ��
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
	    
	    public static String lastWeek(){
	    	   Date date = new Date();
	    	   int year=Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
	    	   int month=Integer.parseInt(new SimpleDateFormat("MM").format(date));
	    	   int day=Integer.parseInt(new SimpleDateFormat("dd").format(date))-3;
	    	  
	    	   if(day<1){
	    	    month-=1;
	    	    if(month==0){
	    	     year-=1;month=12;
	    	    }
	    	    if(month==4||month==6||month==9||month==11){
	    	     day=30+day;
	    	    }else if(month==1||month==3||month==5||month==7||month==8||month==10||month==12)
	    	    {
	    	     day=31+day;
	    	    }else if(month==2){
	    	     if(year%400==0||(year %4==0&&year%100!=0))day=29+day;
	    	     else day=28+day;
	    	    }     
	    	   }
	    	   String y = year+"";String m ="";String d ="";
	    	   if(month<10) m = "0"+month;
	    	   else m=month+"";
	    	   if(day<10) d = "0"+day;
	    	   else d = day+"";
	    	  
	    	   return y+"-"+m+"-"+d;
	    	}
	    static String url = "http://sdk.open.api.igexin.com/apiex.htm";
	    public static NotificationTemplate notificationTemplateDemo(String appId, String appKey, String masterSecret,
				String content)throws Exception{
	        NotificationTemplate template = new NotificationTemplate();
	        // ����APPID��APPKEY
	        template.setAppId(appId);
	        template.setAppkey(appKey);
	        // ����֪ͨ������������
	        template.setTitle("������֪ͨ������");
	       template.setText("������֪ͨ������");
	        // ����֪ͨ��ͼ��
	        template.setLogo("icon.png");
	        // ����֪ͨ������ͼ��
	        template.setLogoUrl("");
	        // ����֪ͨ�Ƿ����壬�𶯣����߿����
	        template.setIsRing(true);
	        template.setIsVibrate(true);
	        template.setIsClearable(true);
	        // ͸����Ϣ���ã�1Ϊǿ������Ӧ�ã��ͻ��˽��յ���Ϣ��ͻ���������Ӧ�ã�2Ϊ�ȴ�Ӧ������
	        template.setTransmissionType(2);
	        template.setTransmissionContent(content);
	        return template;
	    }
	    
	   

		public static void pushInfoAll(String appId, String appKey, String masterSecret,
				 String content) throws Exception {
			IGtPush push = new IGtPush(url, appKey, masterSecret);
			push.connect();
			NotificationTemplate template = notificationTemplateDemo(appId, appKey,masterSecret, content);

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
	    
	    public static String loadJson (String url) {
	        StringBuilder json = new StringBuilder();
	        try {
	            URL urlObject = new URL(url);
	            URLConnection uc = urlObject.openConnection();
	            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
	            String inputLine = null;
	            while ( (inputLine = in.readLine()) != null) {
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
	    
	    public static void push() throws Exception{
	    	String path = loadJson("file:\\E:\\server-backend\\tella-webservice\\WebContent\\META-INF\\getui.json");
			JSONObject json1 = JSONObject.fromObject(path);
			String appId = json1.getString("appId");
			String appKey = json1.getString("appKey");
			String masterSecret = json1.getString("masterSecret");
			/*String content = "Ҽ퓸�����1.0��ʽ�棬�˰汾Ϊ�ڲ����һ���汾����������Ҽ�APP";
			pushInfoAll(appId,appKey,masterSecret,content);*/
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("file:\\E:\\server-backend\\tella-webservice\\WebContent\\WEB-INF\\applicationContext.xml");  
	    	// ApplicationContext context=new FileSystemXmlApplicationContext("E:\\workspace\\tella-webservice\\WebContent\\WEB-INF\\applicationContext.xml"); 
	    	 PushNotificationDao pushNotificationDao = (PushNotificationDao)context.getBean("pushNotificationDao");
	    	 NotificationDao notificationDao = (NotificationDao)context.getBean("notificationDao");
	    	 List<PushNotification> pnList = pushNotificationDao.getPushNotificationByUserid(107l);
	    	 Map<String, Integer> map = new HashMap<String, Integer>();
			if ((pnList != null) && (pnList.size() > 0)) {
				for (PushNotification pn : pnList) {
					int count = notificationDao.getNotificationByRecipientId(pn.getUserId());
					map.put(pn.getClientId(), Integer.valueOf(count));
					System.out.println(pn.getClientId());
				}
			}
			 String content = "Ҽ퓸�����1.2��ʽ�棬��������Ҽ�APP";
	    	// PushNotificationUtil.pushInfoAllFollow(appId, appKey, masterSecret, pnList, map, content);
	    	 System.out.println("success");
	    }
	    
	    public static String sendGet(String url) {
	        String result = "";
	        BufferedReader in = null;
	        try {
	            String urlNameString = url ;
	            URL realUrl = new URL(urlNameString);
	            // �򿪺�URL֮�������
	            URLConnection connection = realUrl.openConnection();
	            // ����ͨ�õ���������
	            connection.setRequestProperty("accept", "*/*");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            connection.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // ����ʵ�ʵ�����
	            connection.connect();
	            // ��ȡ������Ӧͷ�ֶ�
	            Map<String, List<String>> map = connection.getHeaderFields();
	            // �������е���Ӧͷ�ֶ�
	            for (String key : map.keySet()) {
	                System.out.println(key + "--->" + map.get(key));
	            }
	            // ���� BufferedReader����������ȡURL����Ӧ
	            in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("����GET��������쳣��" + e);
	            e.printStackTrace();
	        }
	        // ʹ��finally�����ر�������
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
	                System.out.println("-------�趨Ҫָ������--------");
	            }
	        }, 2);// �趨ָ����ʱ��time,�˴�Ϊ2000����
	    }
	    
	    /**
		 * 
		 * @param ����ͨ�ͻ��ӿڲ���
		 * @param sendsmsaddress
		 * @return
		 * 
		 */
		public String commandID="3";
		public String username="rujiastore";
		public String password="rujiastore5689";
	    public String serviceURL = "http://124.173.70.59:8081/SmsAndMms/mt"; 
	    public static String connectURL(String commString,String sendsmsaddress) {
			String rec_string = "";
			URL url = null;
			HttpURLConnection urlConn = null;
			try {
				url = new URL(sendsmsaddress);  //�������ݵķ��͵�ַ����URL
				urlConn = (HttpURLConnection) url.openConnection(); //������
				urlConn.setConnectTimeout(30000); //���ӳ�ʱ����Ϊ30��
				urlConn.setReadTimeout(30000);	//��ȡ��ʱ����30��
				urlConn.setRequestMethod("POST");	//������Ӧ��ʽΪpost
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

	public String sendSms(String mobile,String content) {
			String res = "";
			try {
				String commString ="Sn="+username+"&Pwd="+password+"&mobile=" + mobile + "&content="+content;
				res = connectURL(commString,serviceURL);
			} catch (Exception e) {
				return "-10000";
			}
			//���÷���ֵ  ��������ֵ
			String resultok = "";
//				//������ʽ
				Pattern pattern = Pattern.compile("<int xmlns=\"http://tempuri.org/\">(.*)</int>");
				Matcher matcher = pattern.matcher(res);
				while (matcher.find()) {
					resultok = matcher.group(1);
				}
			return resultok;
		}
	
	public static Date date = null;
	
	static{
		date = new Date();
	}

	    public static void main(String[] args)throws Exception {
	    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�����ʽ������ʾ����
	    	Timestamp now = new Timestamp(1458203734);//��ȡϵͳ��ǰʱ��
	    	String str = df.format(now);
	    	System.out.println(str);
	    }

}
