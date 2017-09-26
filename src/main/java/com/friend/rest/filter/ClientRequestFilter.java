package com.friend.rest.filter;

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

import com.friend.rest.common.EncryptionUtil;
import com.friend.rest.common.FBEncryption;
import com.friend.rest.common.HttpUtil;
import com.friend.rest.common.ParseFile;
import com.friend.rest.dao.UserDao;
import com.friend.rest.model.User;
import com.google.common.base.Strings;

import net.sf.json.JSONObject;

@Transactional
public class ClientRequestFilter extends OncePerRequestFilter implements Filter {
	private static final Log log = LogFactory.getLog(ClientRequestFilter.class);

//	@Autowired
//	private UserDao userDao;

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

	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain)
			throws ServletException, IOException {
		
		HttpServletRequest request = (HttpServletRequest)req;  
        HttpServletResponse response = (HttpServletResponse)resp; 
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

		if ((uri.equals("/car-friend/v1/users/appsignup"))
				|| (uri.equals("/car-friend/v1/users/auth_code"))
				|| (uri.equals("/car-friend/v1/admin/login"))
				|| (uri.equals("/car-friend/v1/users/signup"))
				|| (uri.equals("/car-friend/v1/basic_params"))
				|| (uri.equals("/car-friend/v1/users/test")) 
				|| (uri.contains("/car-friend/v1/columns"))
				|| (uri.contains("/car-friend/v1/admin"))
				|| (uri.equals("/car-friend/v1/notifications/device"))
				|| (uri.equals("/car-friend/v1/notifications/device/clear"))
				|| (uri.equals("/car-friend/v1/notifications/list"))
				|| (uri.contains("/car-friend/v1/users/login"))
				|| (uri.contains("/car-friend/v1/users/forgot/email"))
				|| (uri.contains("/car-friend/v1/users/forgot/phone"))
				|| (uri.contains("/car-friend/v1/users/phone"))
				|| (uri.contains("/car-friend/v1/users/error")) 
				|| (uri.contains("/car-friend/v1/web"))
				|| (uri.contains("/car-friend/v1/users/homepage_slides"))
				|| (uri.contains("/car-friend/v1/cdnaccesstokens"))
				|| (uri.contains("/car-friend/v1/discover"))
				|| ((uri.contains("/car-friend/v1/collections")) && !uri.contains("/car-friend/v1/collections/follow"))
				|| (uri.contains("/car-friend/v1/users/timesquare_slides"))
				|| (uri.matches("/car-friend/v1/stories/" + regex))
				|| (uri.matches("/car-friend/v1/stories/" + regex + "/comments/count"))
				|| (uri.matches("/car-friend/v1/stories/" + regex + "/comments/" + regex) && method.equals("GET"))
				|| (uri.matches("/car-friend/v1/users/" + regex + "/following"))
				|| (uri.matches("/car-friend/v1/users/" + regex + "/followers"))
				|| (uri.matches("/car-friend/v1/users/" + regex + "/profile_stories"))
				|| (uri.matches("/car-friend/v1/users/" + regex + "/profile_repost"))
				|| (uri.matches("/car-friend/v1/stories/" + regex + "/comments") && method.equals("GET"))
				|| (uri.matches("/car-friend/v1/users/" + regex + "/profile_collections"))
				|| (uri.equals("/car-friend/WebContent/META-INF/qiniu.json"))
				|| (uri.equals("/car-friend/v1/index")) 
				|| (uri.equals("/car-friend/v1/users/get_code"))
				|| (uri.equals("/car-friend/v1/users/get_register_code"))
				|| (uri.equals("/car-friend/v1/users/get_bind_code"))
				|| (uri.equals("/car-friend/v1/users/check_user"))
				|| (uri.equals("/car-friend/v1/users/logout"))
				|| (uri.equals("/car-friend/v1/users/check_token"))
				|| (uri.equals("/car-friend/v1/stories/video"))
				|| (uri.equals("/car-friend/v1/stories/add_story"))
				|| (uri.equals("/car-friend/v1/stories/add_like"))
				|| (uri.equals("/car-friend/v1/users/get_auth"))
				|| (uri.equals("/car-friend/v1/users/fbtoken"))
				|| (uri.equals("/car-friend/v1/users/fbstory"))
				|| (uri.equals("/car-friend/v1/fmap/add_wiki"))) {
			log.info(String.format(String.format("[**No Auth**] Request uri:", new Object[] { uri }), new Object[0]));
			String token = request.getHeader("X-Tella-Request-Token") != null
					? request.getHeader("X-Tella-Request-Token") : "";
//			String fbId = request.getHeader("X-Tella-Request-Userid") != null ? request.getHeader("X-Tella-Request-Userid")
//					: "";


					if (!Strings.isNullOrEmpty(token)) {
						log.info("REQUEST token:------------->>>>>>>>>>>>> " + token);
						Map<String, String> param = new HashMap<String, String>();
						param.put("ip", ip);
						// param.put("device", device);
//						param.put("id", userId);
						param.put("token", token);
						String params = "";
						try {
							params = publicParam(param);
						} catch (Exception e) {
							e.printStackTrace();
							JSONObject jo = new JSONObject();
							jo.put("status", "token invalid");
							jo.put("code", 10623);
							jo.put("error_message", "token invalid");
							PrintWriter writer = response.getWriter();
							writer.write(jo.toString());
						}

						String result = "";
						try {
							result = HttpUtil.sendGet(urlKey + "/customer/info/get-by-token?" + params);
						} catch (Exception e) {
							e.printStackTrace();
							JSONObject jo = new JSONObject();
							jo.put("status", "token invalid");
							jo.put("code", 10623);
							jo.put("error_message", "token invalid");
							PrintWriter writer = response.getWriter();
							writer.write(jo.toString());
						}
						if (!Strings.isNullOrEmpty(result)) {
							JSONObject res_json = JSONObject.fromObject(result);
							int code = res_json.getInt("code");
							if (code == 10000) {
								JSONObject data = res_json.getJSONObject("data");
								String userid = data.getString("userid");
//								response.setHeader("X-Tella-Request-Userid", userid);
								request.setAttribute("X-Tella-Request-Userid", userid);
								filterChain.doFilter(request, response);
								//doFilter(request, response, filterChain);

//								if (Strings.isNullOrEmpty(appVersion)) {
//									filterChain.doFilter(request, response);
//								} else {
//									String path = getClass().getResource("/../../META-INF/version.json").getPath();
//									JSONObject v = ParseFile.parseJson(path);
//									String version = v.getString("version");
//									if (!Strings.isNullOrEmpty(version)) {
//										String[] vArr = version.split("\\.");
//										String[] vaArr = appVersion.split("\\.");
//										if (version.equals(appVersion)) {
//											filterChain.doFilter(request, response);
//										} else {
//											if (!vArr[0].equals(vaArr[0])) {
//												if (Integer.parseInt(vArr[0]) > Integer.parseInt(vaArr[0])) {
//													String url = getServletContext().getContextPath()
//															+ "/v1/users/error/invalid_version";
//													response.sendRedirect(url);
//													return;
//												} else {
//													filterChain.doFilter(request, response);
//												}
//											} else {
//												if (!vArr[1].equals(vaArr[1])) {
//													if (Integer.parseInt(vArr[1]) > Integer.parseInt(vaArr[1])) {
//														String url = getServletContext().getContextPath()
//																+ "/v1/users/error/invalid_version";
//														response.sendRedirect(url);
//														return;
//													} else {
//														filterChain.doFilter(request, response);
//													}
//												} else {
//													if (!vArr[2].equals(vaArr[2])) {
//														if (Integer.parseInt(vArr[2]) > Integer.parseInt(vaArr[2])) {
//															String url = getServletContext().getContextPath()
//																	+ "/v1/users/error/invalid_version";
//															response.sendRedirect(url);
//															return;
//														} else {
//															filterChain.doFilter(request, response);
//														}
//													}
//												}
//											}
//										}
//									}
	//
//								}
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
							}else{
								JSONObject jo = new JSONObject();
								jo.put("status", "token失效");
								jo.put("code", code);
								jo.put("error_message", "token失效");
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
				else{
				filterChain.doFilter(request, response);
			}
		
			
			

		} else {
			if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(method)) {
				filterChain.doFilter(request, response);
			} else {
				String userId = request.getHeader("X-Tella-Request-Userid") != null
						? request.getHeader("X-Tella-Request-Userid") : "";
				String token = request.getHeader("X-Tella-Request-Token") != null
						? request.getHeader("X-Tella-Request-Token") : "";
				String appVersion = request.getHeader("X-Tella-Request-AppVersion") != null
						? request.getHeader("X-Tella-Request-AppVersion") : "";

				if (!Strings.isNullOrEmpty(token)) {
					Map<String, String> param = new HashMap<String, String>();
					param.put("ip", ip);
					// param.put("device", device);
					param.put("id", userId);
					param.put("token", token);
					String params = "";
					try {
						params = publicParam(param);
					} catch (Exception e) {
						e.printStackTrace();
					}

					String result = "";
					try {
						result = HttpUtil.sendGet(urlKey + "/customer/info/get-by-token?" + params);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!Strings.isNullOrEmpty(result)) {
						JSONObject res_json = JSONObject.fromObject(result);
						int code = res_json.getInt("code");
						if (code == 10000) {
							JSONObject data = res_json.getJSONObject("data");
							String userid = data.getString("userid");
//							response.setHeader("X-Tella-Request-Userid", userid);
							request.setAttribute("X-Tella-Request-Userid", userid);
							filterChain.doFilter(request, response);
							//doFilter(request, response, filterChain);

//							if (Strings.isNullOrEmpty(appVersion)) {
//								filterChain.doFilter(request, response);
//							} else {
//								String path = getClass().getResource("/../../META-INF/version.json").getPath();
//								JSONObject v = ParseFile.parseJson(path);
//								String version = v.getString("version");
//								if (!Strings.isNullOrEmpty(version)) {
//									String[] vArr = version.split("\\.");
//									String[] vaArr = appVersion.split("\\.");
//									if (version.equals(appVersion)) {
//										filterChain.doFilter(request, response);
//									} else {
//										if (!vArr[0].equals(vaArr[0])) {
//											if (Integer.parseInt(vArr[0]) > Integer.parseInt(vaArr[0])) {
//												String url = getServletContext().getContextPath()
//														+ "/v1/users/error/invalid_version";
//												response.sendRedirect(url);
//												return;
//											} else {
//												filterChain.doFilter(request, response);
//											}
//										} else {
//											if (!vArr[1].equals(vaArr[1])) {
//												if (Integer.parseInt(vArr[1]) > Integer.parseInt(vaArr[1])) {
//													String url = getServletContext().getContextPath()
//															+ "/v1/users/error/invalid_version";
//													response.sendRedirect(url);
//													return;
//												} else {
//													filterChain.doFilter(request, response);
//												}
//											} else {
//												if (!vArr[2].equals(vaArr[2])) {
//													if (Integer.parseInt(vArr[2]) > Integer.parseInt(vaArr[2])) {
//														String url = getServletContext().getContextPath()
//																+ "/v1/users/error/invalid_version";
//														response.sendRedirect(url);
//														return;
//													} else {
//														filterChain.doFilter(request, response);
//													}
//												}
//											}
//										}
//									}
//								}
//
//							}
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

				}else{
					JSONObject jo = new JSONObject();
					jo.put("status", "token不能为空");
					jo.put("code", 10624);
					jo.put("error_message", "token不能为空");
					PrintWriter writer = response.getWriter();
					writer.write(jo.toString());
				}

			}

		}
	}
}
