 package com.revolution.rest.service;
 
 import com.qiniu.api.auth.digest.Mac;
 import com.qiniu.api.rs.PutPolicy;
import com.revolution.rest.common.ParseFile;

import net.sf.json.JSONObject;
 import org.springframework.transaction.annotation.Transactional;
 
 @Transactional
 public class CDNAccessServiceImpl
   implements CDNAccessService
 {
   public JSONObject getQiNiuToken()
   {
     JSONObject qiniuJson = new JSONObject();
     try {
       String path = getClass().getResource("/../../META-INF/qiniu.json")
         .getPath();
       JSONObject jsonObject = ParseFile.parseJson(path);
       String ak = jsonObject.getString("ak");
       String sk = jsonObject.getString("sk");
       String bucket = jsonObject.getString("bucket");
       String token = null;
       token = getToken(ak, sk, bucket);
       String meta = jsonObject.getString("meta");
 
       qiniuJson.put("upload_token", token);
       qiniuJson.put("meta", meta);
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
     Mac mac = new Mac(ak, sk);
 
     PutPolicy policy = new PutPolicy(bucket);
 
     String token = policy.token(mac);
 
     return token;
   }
 }

