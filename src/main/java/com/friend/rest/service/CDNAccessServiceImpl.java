 package com.friend.rest.service;
 
 import java.util.Calendar;

import org.springframework.transaction.annotation.Transactional;

import com.friend.rest.common.HttpUtil;
import com.friend.rest.common.ParseFile;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;

import net.sf.json.JSONObject;
 
 @Transactional
 public class CDNAccessServiceImpl
   implements CDNAccessService
 {
   public JSONObject getQiNiuToken()
   {
     JSONObject qiniuJson = new JSONObject();
     try {
       String path = getClass().getResource("/../../META-INF/user_centre.json")
         .getPath();
       JSONObject jsonObject = ParseFile.parseJson(path);
       String url = jsonObject.getString("qiniu_url");
       StringBuffer sb = new StringBuffer();
       sb.append("bucket=content");
       String result = HttpUtil.sendGetStr(url+"/image/common/get-token", sb.toString());
       JSONObject json = JSONObject.fromObject(result);
       int code = json.getInt("code");
       if(code == 10000){
    	   JSONObject data = json.getJSONObject("data");
    	   qiniuJson.put("upload_token", data.getString("token"));
       }
       
       return qiniuJson;
     } catch (Exception e) {
       qiniuJson.put("status", "invalid_request");
       qiniuJson.put("code", Integer.valueOf(10010));
       qiniuJson.put("error_message", "Invalid payload parameters");
     }return qiniuJson;
   }
 
   public String getToken(String ak, String sk, String bucket)
     throws Exception
   {
//	 Auth auth = Auth.create(ak,sk);
//	 Calendar cal = Calendar.getInstance();
//	 
//	 String token = auth.uploadToken(bucket, "key", 3600, new StringMap().put("deadline",1000));
     Mac mac = new Mac(ak, sk);
 
     PutPolicy policy = new PutPolicy(bucket);
     Calendar cal = Calendar.getInstance();
     cal.add(Calendar.HOUR_OF_DAY, 1);
     policy.expires = 3600 *24;
     String token = policy.token(mac);
     
 
     return token;
   }
 }

