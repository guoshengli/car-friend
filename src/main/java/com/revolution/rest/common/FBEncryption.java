package com.revolution.rest.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by 代勋 on 2016-11-24
 *
 * 依赖包SecretUtils.java下载地址：http://mvnrepository.com/artifact/commons-codec/commons-codec/1.10
 */
public class FBEncryption {

    public static final String SIGN = "sign";
    // 公钥
    private String apiKey;
    // 私钥
    private String apiSecretKey;

    public FBEncryption(String apiKey, String apiSecretKey) {
        this.apiKey = apiKey;
        this.apiSecretKey = apiSecretKey;
    }

    private static byte[] hmac_sha1(String value, String key) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            return mac.doFinal(value.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String httpBuildQueryRFC3986(Map<String, String> sortedMap) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        for (String key : sortedMap.keySet()) {
            sb.append(key);
            sb.append("=");
            sb.append(encodeRFC3986(sortedMap.get(key)));
            sb.append("&");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private static String encodeRFC3986(String str) throws UnsupportedEncodingException {
        str = str.replace("+", "%2B");
        str = URLDecoder.decode(str, "UTF-8");
        str = URLEncoder.encode(str, "UTF-8");
        return str.replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });

        sortMap.putAll(map);

        return sortMap;
    }

    private static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 给参数列表添加签名
    public Map<String, String> signature(Map<String, String> params) throws UnsupportedEncodingException {
        params.put("api_key", apiKey);
        params.put("nonce", MD5(String.valueOf(System.currentTimeMillis())));

        Map<String, String> sortedMap = sortMapByKey(params);
        String baseStr = httpBuildQueryRFC3986(sortedMap);
        sortedMap.put("sign", new String(org.apache.commons.codec.binary.Base64.encodeBase64(hmac_sha1(baseStr, apiSecretKey))).replace('+', '-').replace('/', '_'));
        return sortedMap;
    }

    // 校验签名
    public boolean checkSignature(Map<String, String> params) throws UnsupportedEncodingException {
        String sign = params.remove(SIGN);
        try {
            if (sign != null && !sign.trim().isEmpty()) {
                Map<String, String> sortedMap = sortMapByKey(params);
                String baseStr = httpBuildQueryRFC3986(sortedMap);

                String mySign = new String(org.apache.commons.codec.binary.Base64.encodeBase64(hmac_sha1(baseStr, apiSecretKey))).replace('+', '-').replace('/', '_');
                if (sign.equals(mySign)) {
                    return true;
                }
            }
            return false;
        } finally {
            params.put(SIGN, sign);
        }
    }
}
