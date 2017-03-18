package com.revolution.rest.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.base.Strings;
import com.revolution.rest.common.EncryptionUtil;
import com.revolution.rest.common.FBEncryption;
import com.revolution.rest.common.HttpUtil;
import com.revolution.rest.common.ParseFile;
import com.revolution.rest.dao.UserDao;
import com.revolution.rest.model.User;

import net.sf.json.JSONObject;

@Transactional
public class ClientRequestFilter extends OncePerRequestFilter implements Filter {
	private static final Log log = LogFactory.getLog(ClientRequestFilter.class);

	@Autowired
	private UserDao userDao;

	public String publicParam(Map<String, String> param) throws Exception {
		FBEncryption fb = new FBEncryption("20161206100527xEhf0s8Vj3j_uSMrI1eOHU--8k1LwR0o",
				"20161206100534Z_qxIRa6BBMophlaZMNwSVwJMGmL_ptB");

		param.put("channel", "40");
		Map<String, String> map = fb.signature(param);
		boolean bool = fb.checkSignature(map);
		System.out.println("bool--->>>" + bool);
		Set<String> keys = map.keySet();
		Iterator<String> iter = keys.iterator();
		StringBuffer sb = new StringBuffer();
		while (iter.hasNext()) {
			String key = iter.next();
			sb.append(key + "=" + map.get(key) + "&");
		}
		String res = sb.toString();
		String result = res.substring(0, res.length() - 1);
		return result;
	}

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		log.debug("Applying access filter...");
		log.info("REQUEST URI: " + requestURI);
		
		response.setCharacterEncoding("utf-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
		response.setHeader("Access-Control-Allow-Headers",
				"authorization, content-type, x-tella-request-appversion, x-tella-request-provider, x-tella-request-timestamp, x-tella-request-token"
						+ ", x-tella-request-userid, x-tella-request-fbid, x-tella-request-fbtoken, x-tella-request-device");
		String uri = request.getRequestURI();
		String regex = "[0-9]+";
		String method = request.getMethod();
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		log.info("REQUEST IP:------------->>>>>>>>>>>>> " + ip);
		String device = request.getHeader("X-Tella-Request-Device");
		String urlPath = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(urlPath);
		String urlKey = jsonObject.getString("url");

		if ((uri.equals("/revolution-fashion/v1/users/appsignup"))
				|| (uri.equals("/revolution-fashion/v1/users/auth_code"))
				|| (uri.equals("/revolution-fashion/v1/users/signup"))
				|| (uri.equals("/revolution-fashion/v1/basic_params"))
				|| (uri.equals("/revolution-fashion/v1/users/test")) || (uri.contains("/revolution-fashion/v1/columns"))
				|| (uri.equals("/revolution-fashion/v1/notifications/notifications_info"))
				|| (uri.contains("/revolution-fashion/v1/users/login"))
				|| (uri.contains("/revolution-fashion/v1/users/forgot/email"))
				|| (uri.contains("/revolution-fashion/v1/users/forgot/phone"))
				|| (uri.contains("/revolution-fashion/v1/users/phone"))
				|| (uri.contains("/revolution-fashion/v1/users/error")) 
				|| (uri.contains("/revolution-fashion/v1/web"))
				|| (uri.contains("/revolution-fashion/v1/users/homepage_slides"))
				|| (uri.contains("/revolution-fashion/v1/cdnaccesstokens"))
				|| (uri.contains("/revolution-fashion/v1/discover"))
				|| (uri.contains("/revolution-fashion/v1/collections"))
				|| (uri.contains("/revolution-fashion/v1/users/timesquare_slides"))
				|| (uri.matches("/revolution-fashion/v1/stories/" + regex))
				|| (uri.matches("/revolution-fashion/v1/stories/" + regex + "/comments/count"))
				|| (uri.matches("/revolution-fashion/v1/stories/" + regex + "/comments/" + regex))
				|| (uri.matches("/revolution-fashion/v1/users/" + regex + "/following"))
				|| (uri.matches("/revolution-fashion/v1/users/" + regex + "/followers"))
				|| (uri.matches("/revolution-fashion/v1/users/" + regex + "/profile_stories"))
				|| (uri.matches("/revolution-fashion/v1/users/" + regex + "/profile_repost"))
				|| (uri.matches("/revolution-fashion/v1/stories/" + regex + "/comments"))
				|| (uri.matches("/revolution-fashion/v1/users/" + regex + "/profile_collections"))
				|| (uri.equals("/revolution-fashion/WebContent/META-INF/qiniu.json"))
				|| (uri.equals("/revolution-fashion/v1/admin/exception"))
				|| (uri.equals("/revolution-fashion/v1/index")) || (uri.equals("/revolution-fashion/v1/users/get_code"))
				|| (uri.equals("/revolution-fashion/v1/users/get_register_code"))
				|| (uri.equals("/revolution-fashion/v1/users/get_bind_code"))
				|| (uri.equals("/revolution-fashion/v1/users/check_user"))
				|| (uri.equals("/revolution-fashion/v1/users/logout"))
				|| (uri.equals("/revolution-fashion/v1/users/check_token"))
				|| (uri.equals("/revolution-fashion/v1/stories/video"))
				|| (uri.equals("/revolution-fashion/v1/stories/add_story"))
				|| (uri.equals("/revolution-fashion/v1/users/get_auth"))
				|| (uri.equals("/revolution-fashion/v1/users/fbtoken"))
				|| (uri.equals("/revolution-fashion/v1/users/fbstory"))
				|| (uri.equals("/revolution-fashion/v1/fmap/add_wiki"))
				|| (uri.contains("/revolution-fashion/v1/fmap"))) {
			log.info(String.format(String.format("[**No Auth**] Request uri:", new Object[] { uri }), new Object[0]));
			String fbToken = request.getHeader("X-Tella-Request-FbToken") != null
					? request.getHeader("X-Tella-Request-FbToken") : "";
			String fbId = request.getHeader("X-Tella-Request-FbID") != null ? request.getHeader("X-Tella-Request-FbID")
					: "";

			if (!Strings.isNullOrEmpty(fbToken) && 
					!Strings.isNullOrEmpty(fbId) && !fbId.equals("0")) {
				Map<String, String> param = new HashMap<String, String>();
				param.put("ip", ip);
				// param.put("device", device);
				param.put("id", fbId);
				param.put("token", fbToken);
				String params = "";
				try {
					params = publicParam(param);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String result = "";
				try {
					result = HttpUtil.sendGet(urlKey + "/customer/account/check-token?" + params);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JSONObject res_json = JSONObject.fromObject(result);
				int code = res_json.getInt("code");
				if (code == 10000) {
					filterChain.doFilter(request, response);
				} else if (code == 10001) {
					JSONObject jo = new JSONObject();
					jo.put("status", "token invalid"+ip);
					jo.put("code", 10623);
					jo.put("error_message", "token invalid"+ip);
					PrintWriter writer = response.getWriter();
					writer.write(jo.toString());
				} else if (code == 10002) {
					JSONObject jo = new JSONObject();
					jo.put("status", "token invalid"+ip);
					jo.put("code", 10623);
					jo.put("error_message", "token invalid"+ip);
					PrintWriter writer = response.getWriter();
					writer.write(jo.toString());
				}else if (code == 10110) {
					JSONObject jo = new JSONObject();
					jo.put("status", "token invalid"+ip);
					jo.put("code", 10623);
					jo.put("error_message", "token invalid"+ip);
					PrintWriter writer = response.getWriter();
					writer.write(jo.toString());
				}
				
			}else{
				filterChain.doFilter(request, response);
			}
		
			
			

		} else {
			if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(method)) {
				filterChain.doFilter(request, response);
			} else {
				String userId = request.getHeader("X-Tella-Request-Userid") != null
						? request.getHeader("X-Tella-Request-Userid") : "";
				String timestamp = request.getHeader("X-Tella-Request-Timestamp") != null
						? request.getHeader("X-Tella-Request-Timestamp") : "";
				String token = request.getHeader("X-Tella-Request-Token") != null
						? request.getHeader("X-Tella-Request-Token") : "";
				String appVersion = request.getHeader("X-Tella-Request-AppVersion") != null
						? request.getHeader("X-Tella-Request-AppVersion") : "";
				String fbToken = request.getHeader("X-Tella-Request-FbToken") != null
						? request.getHeader("X-Tella-Request-FbToken") : "";
				String fbId = request.getHeader("X-Tella-Request-FbID") != null
						? request.getHeader("X-Tella-Request-FbID") : "";

				if (!Strings.isNullOrEmpty(fbToken) && !Strings.isNullOrEmpty(fbId) && !fbId.equals("0")) {
					Map<String, String> param = new HashMap<String, String>();
					param.put("ip", ip);
					// param.put("device", device);
					param.put("id", fbId);
					param.put("token", fbToken);
					String params = "";
					try {
						params = publicParam(param);
					} catch (Exception e) {
						e.printStackTrace();
					}

					String result = "";
					try {
						result = HttpUtil.sendGet(urlKey + "/customer/account/check-token?" + params);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (!Strings.isNullOrEmpty(result)) {
						JSONObject res_json = JSONObject.fromObject(result);
						int code = res_json.getInt("code");
						if (code == 10000) {
							if ((Strings.isNullOrEmpty(userId)) || (Strings.isNullOrEmpty(timestamp))
									|| (Strings.isNullOrEmpty(token))) {
								log.error(String.format(
										"[**Auth Failure**] Missing params: userId:%s timestamp:%s token:%s",
										new Object[] { userId, timestamp, token }));

								String url = request.getServletContext().getContextPath() + "/v1/users/error/noData";
								response.sendRedirect(url);
								log.debug("*** no auth /v1/users/error/noData ****");
								return;
							}
							User user = null;
							if (!Strings.isNullOrEmpty(userId)) {
								user = (User) this.userDao.get(Long.parseLong(userId));
							}

							if (user == null) {
								log.error(String.format("[**Auth Failure**] invalid userId:%s timestamp:%s token:%s",
										new Object[] { userId, timestamp, token }));
								String url = getServletContext().getContextPath() + "/v1/users/error/noUser";
								response.sendRedirect(url);
								log.debug("*** no User /v1/users/error/noData ****");
								return;
							}
							if ((!user.getUser_type().equals("admin")) && (!user.getUser_type().equals("super_admin"))
									&& (!user.getUser_type().equals("official"))
									&& (uri.contains("/revolution-fashion/v1/admin"))) {
								String url = getServletContext().getContextPath() + "/v1/users/error/noAuthority";
								response.sendRedirect(url);
								log.debug("*** no token /v1/users/error/noData ****");
								return;
							}

							String raw = user.getId() + timestamp;
							String generatedToken = EncryptionUtil.hashMessage(raw);
							log.debug("[**Debug Info**] raw: " + raw);
							log.debug("[**Debug Info**] server calculated hash is: " + generatedToken);
							log.debug("[**Debug Info**] provided hash is: " + token);

							if (!token.equals(generatedToken)) {
								log.error(String.format(
										"[**Auth Failure**] invalid token for userId:%s timestamp:%s token:%s",
										new Object[] { userId, timestamp, token }));
								String url = getServletContext().getContextPath() + "/v1/users/error/invalid_token";
								response.sendRedirect(url);
								log.debug("*** no token /v1/users/error/noData ****");
								return;
							}

							if (Strings.isNullOrEmpty(appVersion)) {
								filterChain.doFilter(request, response);
							} else {
								String path = getClass().getResource("/../../META-INF/version.json").getPath();
								JSONObject v = ParseFile.parseJson(path);
								String version = v.getString("version");
								if (!Strings.isNullOrEmpty(version)) {
									String[] vArr = version.split("\\.");
									String[] vaArr = appVersion.split("\\.");
									if (version.equals(appVersion)) {
										filterChain.doFilter(request, response);
									} else {
										if (!vArr[0].equals(vaArr[0])) {
											if (Integer.parseInt(vArr[0]) > Integer.parseInt(vaArr[0])) {
												String url = getServletContext().getContextPath()
														+ "/v1/users/error/invalid_version";
												response.sendRedirect(url);
												return;
											} else {
												filterChain.doFilter(request, response);
											}
										} else {
											if (!vArr[1].equals(vaArr[1])) {
												if (Integer.parseInt(vArr[1]) > Integer.parseInt(vaArr[1])) {
													String url = getServletContext().getContextPath()
															+ "/v1/users/error/invalid_version";
													response.sendRedirect(url);
													return;
												} else {
													filterChain.doFilter(request, response);
												}
											} else {
												if (!vArr[2].equals(vaArr[2])) {
													if (Integer.parseInt(vArr[2]) > Integer.parseInt(vaArr[2])) {
														String url = getServletContext().getContextPath()
																+ "/v1/users/error/invalid_version";
														response.sendRedirect(url);
														return;
													} else {
														filterChain.doFilter(request, response);
													}
												}
											}
										}
									}
								}

							}
						} else if (code == 10001) {
							JSONObject jo = new JSONObject();
							jo.put("status", "token invalid");
							jo.put("code", 10623);
							jo.put("error_message", "token invalid");
							PrintWriter writer = response.getWriter();
							writer.write(jo.toString());
						} else if (code == 10002) {
							JSONObject jo = new JSONObject();
							jo.put("status", "token invalid");
							jo.put("code", 10623);
							jo.put("error_message", "token invalid");
							PrintWriter writer = response.getWriter();
							writer.write(jo.toString());
						}else if (code == 10110) {
							JSONObject jo = new JSONObject();
							jo.put("status", "token invalid");
							jo.put("code", 10623);
							jo.put("error_message", "token invalid");
							PrintWriter writer = response.getWriter();
							writer.write(jo.toString());
						}
					} else {
						JSONObject jo = new JSONObject();
						jo.put("status", "token invalid");
						jo.put("code", 10623);
						jo.put("error_message", "token invalid");
						PrintWriter writer = response.getWriter();
						writer.write(jo.toString());
					}

				}

			}

		}
	}
}
