package com.coatardbul.baseCommon.interceptor;

import com.coatardbul.baseCommon.constants.Constant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
@Slf4j
public class FeignConfiguration implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		try {
			//外部通过页面或者postman调用的方式不会异常，只有分布式任务调用才会异常
			HttpServletRequest orginalRequest = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = attributes.getRequest();
			if(null != attributes) {
				String token = request.getHeader("token");
				template.header("token", token);
			}
		}catch (Exception e) {
			template.header("token", Constant.INVINCIBLE_TOKEN);
			log.info("分布任务调用feign标记");
		}


	}
}
