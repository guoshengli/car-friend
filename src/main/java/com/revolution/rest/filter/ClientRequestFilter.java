package com.revolution.rest.filter;

import com.google.common.base.Strings;
import com.revolution.rest.common.EncryptionUtil;
import com.revolution.rest.common.ParseFile;
import com.revolution.rest.dao.UserDao;
import com.revolution.rest.model.User;

import net.sf.json.JSONObject;

import java.io.IOException;
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

@Transactional
public class ClientRequestFilter extends OncePerRequestFilter implements Filter {
	private static final Log log = LogFactory.getLog(ClientRequestFilter.class);

	@Autowired
	private UserDao userDao;

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		log.debug("Applying access filter...");
		log.info("REQUEST URI: " + requestURI);

		String uri = request.getRequestURI();
		String regex = "[0-9]+";
		if ((uri.equals("/revolution-fashion/v1/users/appsignup")) 
				|| (uri.equals("/revolution-fashion/v1/users/signup"))
				|| (uri.equals("/revolution-fashion/v1/basic_params"))
				||	(uri.equals("/revolution-fashion/v1/users/test"))
				||	(uri.equals("/revolution-fashion/v1/notifications/notifications_info"))
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
				|| (uri.matches("/revolution-fashion/v1/stories/"+regex))
				|| (uri.matches("/revolution-fashion/v1/stories/"+regex+"/comments/count"))
				|| (uri.matches("/revolution-fashion/v1/users/"+regex+"/following"))
				|| (uri.matches("/revolution-fashion/v1/users/"+regex+"/followers"))
				|| (uri.matches("/revolution-fashion/v1/users/"+regex+"/profile_stories"))
				|| (uri.matches("/revolution-fashion/v1/users/"+regex+"/profile_repost"))
				|| (uri.matches("/revolution-fashion/v1/stories/"+regex+"/comments"))
				|| (uri.matches("/revolution-fashion/v1/users/"+regex+"/profile_collections"))
				|| (uri.equals("/revolution-fashion/WebContent/META-INF/qiniu.json"))
				||(uri.equals("/revolution-fashion/v1/admin/exception"))) {
			log.info(String.format(String.format("[**No Auth**] Request uri:", new Object[] { uri }), new Object[0]));

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
			if ((Strings.isNullOrEmpty(userId)) || (Strings.isNullOrEmpty(timestamp))
					|| (Strings.isNullOrEmpty(token))) {
				log.error(String.format("[**Auth Failure**] Missing params: userId:%s timestamp:%s token:%s",
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
					&& (uri.contains("/revolution-fashion/v1/admin"))) {
				String url = getServletContext().getContextPath() + "/v1/users/error/noAuthority";
				response.sendRedirect(url);
				log.debug("*** no token /v1/users/error/noData ****");
				return;
			}
			

			String raw = user.getId() + user.getPassword() + timestamp;
			String generatedToken = EncryptionUtil.hashMessage(raw);
			log.debug("[**Debug Info**] raw: " + raw);
			log.debug("[**Debug Info**] server calculated hash is: " + generatedToken);
			log.debug("[**Debug Info**] provided hash is: " + token);

			if (!token.equals(generatedToken)) {
				log.error(String.format("[**Auth Failure**] invalid token for userId:%s timestamp:%s token:%s",
						new Object[] { userId, timestamp, token }));
				String url = getServletContext().getContextPath() + "/v1/users/error/invalid_token";
				response.sendRedirect(url);
				log.debug("*** no token /v1/users/error/noData ****");
				return;
			}
			
			
			if(Strings.isNullOrEmpty(appVersion)){
				filterChain.doFilter(request, response);
			}else{
				String path = getClass().getResource("/../../META-INF/version.json").getPath();
				JSONObject v = ParseFile.parseJson(path);
				String version = v.getString("version");
				if(!Strings.isNullOrEmpty(version)){
					String[] vArr = version.split("\\.");
					String[] vaArr = appVersion.split("\\.");
					if(version.equals(appVersion)){
						filterChain.doFilter(request, response);
					}else{
						if(!vArr[0].equals(vaArr[0])){
							if(Integer.parseInt(vArr[0]) > Integer.parseInt(vaArr[0])){
								String url = getServletContext().getContextPath() + "/v1/users/error/invalid_version";
								response.sendRedirect(url);
								return;
							}else{
								filterChain.doFilter(request, response);
							}
						}else{
							if(!vArr[1].equals(vaArr[1])){
								if(Integer.parseInt(vArr[1]) > Integer.parseInt(vaArr[1])){
									String url = getServletContext().getContextPath() + "/v1/users/error/invalid_version";
									response.sendRedirect(url);
									return;
								}else{
									filterChain.doFilter(request, response);
								}
							}else{
								if(!vArr[2].equals(vaArr[2])){
									if(Integer.parseInt(vArr[2]) > Integer.parseInt(vaArr[2])){
										String url = getServletContext().getContextPath() + "/v1/users/error/invalid_version";
										response.sendRedirect(url);
										return;
									}else{
										filterChain.doFilter(request, response);
									}
								}
							}
						}
					}
				}
				
			}
			
		}
	}
}
