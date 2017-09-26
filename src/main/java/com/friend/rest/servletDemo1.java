package com.friend.rest;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.friend.rest.common.LogRecord;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class servletDemo1 extends HttpServlet {
    public servletDemo1() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//�����̨��/logs/info/infoLog.txt��д����
		LogRecord.info("�û���¼�ɹ���");
		
		//�����̨��/logs/error/errorLog.txt��д����
		LogRecord.error("����û�����̫���ˣ�");
	}
	
	public static void execute() throws Exception {
        System.out.println("execute...");
        throw new Exception();
    }

    public static void main(String[] args) throws Exception {
    	String str = "{"
				+ "\"carId\" : \"100\","
				+ "\"serviceName\" : \"testPage.php\","
			    +"\"systemParameterInfo\" : {"
			    + "\"appVersion\" : \"1.0\","
			    + "\"clientInfo\" : {"
			    + "\"clientIp\" : \"169.254.223.2\","
			    + "\"deviceBrand\" : \"Apple\","
			    + "\"deviceId\" : \"F70A0056-302C-4BA0-8264-400E4FFAB266\","
			    + "\"deviceMode\" : \"Simulator\","
			    + "\"os\" : \"iOS 9.2\","
			    + "\"screenHeight\" : \"736.000000\","
			    + "\"screenWidth\" : \"414.000000\","
			    + "},"
			    + "\"refId\" : \"AppStore\","
			    + "\"reqTime\" : \"1458121175\","
			    + "\"version\" : \"1.0.0\","
			    + "}"
			    + "}";

    	JSONObject json = JSONObject.fromObject(str);
    	JSONObject systemParameterInfo = json.getJSONObject("systemParameterInfo");
    	systemParameterInfo.put("clientInfo", "aaa");
    	System.out.println(json.toString());
    }
	

}
