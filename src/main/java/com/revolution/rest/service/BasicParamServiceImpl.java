package com.revolution.rest.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.ws.rs.core.Response;

import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;

import net.sf.json.JSONObject;

public class BasicParamServiceImpl implements BasicParamService {

	@Override
	public Response getBasicParams() {
		JSONObject param = new JSONObject();
		JSONObject qiniuJson = new JSONObject();
		String path = getClass().getResource("/../../META-INF/qiniu.json").getPath();
		
		JSONObject qiniu = parseJson(path);
		String ak = qiniu.getString("ak");
		String sk = qiniu.getString("sk");
		String bucket = qiniu.getString("bucket");
		Object obj = qiniu.get("meta");
		String pathUrl = getClass().getResource("/../../META-INF/url.json").getPath();
		JSONObject url = parseJson(pathUrl);
		String share_url_prefix = url.getString("url");
		String pathPhone = getClass().getResource("/../../META-INF/apple.json").getPath();
		JSONObject phone = parseJson(pathPhone);
		String appstore_link = phone.getString("appstore_link");
		String token = null;
		try {
			token = getToken(ak, sk, bucket);
		} catch (Exception e) {
			e.printStackTrace();
		}
		qiniuJson.put("upload_token", token);
		qiniuJson.put("meta",obj);
		param.put("share_url_prefix",share_url_prefix);
		param.put("appstore_link",appstore_link);
		param.put("qiniu_params", qiniuJson);
		return Response.status(Response.Status.OK).entity(param).build();
	}
	
	public String getToken(String ak, String sk, String bucket) throws Exception {
		Mac mac = new Mac(ak, sk);
		PutPolicy policy = new PutPolicy(bucket);
		String token = policy.token(mac);
		return token;
	}
	
	public JSONObject parseJson(String path) {
		String sets = ReadFile(path);
		JSONObject jo = JSONObject.fromObject(sets);
		return jo;
	}
	
	public String ReadFile(String path) {
		File file = new File(path);
		BufferedReader reader = null;
		String laststr = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;

			while ((tempString = reader.readLine()) != null) {
				laststr = laststr + tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();

			if (reader != null)
				try {
					reader.close();
				} catch (IOException localIOException1) {
				}
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException localIOException2) {
				}
		}
		return laststr;
	}

}
