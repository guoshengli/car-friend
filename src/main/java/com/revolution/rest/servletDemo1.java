package com.revolution.rest;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revolution.rest.common.LogRecord;

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

    	String str = "{123,345}";
    	str = str.substring(1,str.length()-1);
    	System.out.println(str);
    }
	

}
