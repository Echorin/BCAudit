package com.oschain.fastchaindb.common;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CORSFilter implements Filter {
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
	    HttpServletResponse response = (HttpServletResponse) res;

//        response.setHeader("Connection","Keep-Alive");
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
//        response.setContentType("textml;charset=UTF-8");

	    response.setHeader("Access-Control-Allow-Credentials","true");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Max-Age", "3600");
	    response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");


		//权限校验(可以继承BaseController)//extends BaseController
//		if (checkPermisson){
//			String reqUrl = request.getRequestURI();
//			logger.debug("getRequestURI"+reqUrl);
//			if (checkUrl.indexOf(reqUrl)<0){
//				boolean check=false;
//				Object attr=request.getSession().getAttribute(USER_LOGIN_CHECK_TAG);
//				if (attr!=null){
//					check = (boolean) attr;
//				}
//				if (!check){
//					writeErrMsg(response,ErrorEnum.PERMISSION_DENIED);
//					return;
//				}
//			}
//		}

	    chain.doFilter(req, res);
	}
	
	public void init(FilterConfig filterConfig) {
		
	}
	
	public void destroy() {
		
	}

}
