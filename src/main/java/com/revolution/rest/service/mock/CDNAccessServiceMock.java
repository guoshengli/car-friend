/*    */ package com.revolution.rest.service.mock;
/*    */ 
/*    */ import com.qiniu.api.auth.digest.Mac;
/*    */ import com.qiniu.api.rs.PutPolicy;
import com.revolution.rest.service.CDNAccessService;

/*    */ import java.io.BufferedReader;
/*    */ import java.io.File;
/*    */ import java.io.FileReader;
/*    */ import java.io.IOException;
/*    */ import java.net.URL;
/*    */ import net.sf.json.JSONObject;
/*    */ 
/*    */ public class CDNAccessServiceMock
/*    */   implements CDNAccessService
/*    */ {
/*    */   public JSONObject getQiNiuToken()
/*    */   {
/* 18 */     JSONObject qiniuJson = new JSONObject();
/*    */     try {
/* 20 */       String path = getClass().getResource("/../../META-INF/qiniu.json")
/* 21 */         .getPath();
/* 22 */       JSONObject jsonObject = parseJson(path);
/* 23 */       String ak = jsonObject.getString("ak");
/* 24 */       String sk = jsonObject.getString("sk");
/* 25 */       String bucket = jsonObject.getString("bucket");
/* 26 */       String token = null;
/* 27 */       token = getToken(ak, sk, bucket);
/* 28 */       String meta = jsonObject.getString("meta");
/*    */ 
/* 30 */       qiniuJson.put("upload_token", token);
/* 31 */       qiniuJson.put("meta", meta);
/* 32 */       return qiniuJson;
/*    */     } catch (Exception e) {
/* 34 */       qiniuJson.put("status", "invalid_request");
/* 35 */       qiniuJson.put("code", Integer.valueOf(10010));
/* 36 */       qiniuJson.put("error_message", "Invalid payload parameters");
/* 37 */     }return qiniuJson;
/*    */   }
/*    */ 
/*    */   public JSONObject parseJson(String path)
/*    */   {
/* 46 */     String sets = ReadFile(path);
/* 47 */     JSONObject jo = JSONObject.fromObject(sets);
/* 48 */     return jo;
/*    */   }
/*    */ 
/*    */   public String ReadFile(String path)
/*    */   {
/* 53 */     File file = new File(path);
/* 54 */     BufferedReader reader = null;
/* 55 */     String laststr = "";
/*    */     try
/*    */     {
/* 58 */       reader = new BufferedReader(new FileReader(file));
/* 59 */       String tempString = null;
/*    */ 
/* 61 */       while ((tempString = reader.readLine()) != null)
/*    */       {
/* 63 */         laststr = laststr + tempString;
/*    */       }
/* 65 */       reader.close();
/*    */     } catch (IOException e) {
/* 67 */       e.printStackTrace();
/*    */ 
/* 69 */       if (reader != null)
/*    */         try {
/* 71 */           reader.close();
/*    */         }
/*    */         catch (IOException localIOException1)
/*    */         {
/*    */         }
/*    */     }
/*    */     finally
/*    */     {
/* 69 */       if (reader != null)
/*    */         try {
/* 71 */           reader.close();
/*    */         }
/*    */         catch (IOException localIOException2) {
/*    */         }
/*    */     }
/* 76 */     return laststr;
/*    */   }
/*    */ 
/*    */   public String getToken(String ak, String sk, String bucket) throws Exception
/*    */   {
/* 81 */     Mac mac = new Mac(ak, sk);
/*    */ 
/* 83 */     PutPolicy policy = new PutPolicy(bucket);
/*    */ 
/* 85 */     String token = policy.token(mac);
/*    */ 
/* 87 */     return token;
/*    */   }
/*    */ }

/* Location:           E:\project\tella-webservice\WEB-INF\classes\
 * Qualified Name:     com.tella.rest.service.mock.CDNAccessServiceMock
 * JD-Core Version:    0.6.2
 */