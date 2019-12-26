package com.wx.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import com.wx.description.WxParameters;
import com.wx.description.WxRequest;
import com.wx.entity.TestGlobal;

/**
 * @author wuxi
 * @date 2018年11月2日
 */
public class ApiUtils {

	public static <T> T create(Class<T> wx) {
		
		//创建一个代理类
		return (T) Proxy.newProxyInstance(wx.getClassLoader(), new Class[] { wx }, new InvocationHandler() {
			
			//代理的方法，代理方法接受的参数
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				
				Annotation[] annotations = method.getAnnotations();
				TestGlobal testGlobal = new TestGlobal();
				if (annotations.length == 0) {
					throw new RuntimeException(String.format("%s方法未配置请求类型注解", method.getName()));
				}		
				//参数注解
				String protocol = ((WxRequest) annotations[0]).protocol();
				testGlobal.setProtocol(protocol);
				String description = ((WxRequest) annotations[0]).description();
			    testGlobal.setDescription(description);

				String wmethod = ((WxRequest) annotations[0]).wmethod();
				testGlobal.setWmethod(wmethod);

				// 方法注解
				Annotation[][] parameters = method.getParameterAnnotations();
				Integer length = parameters.length;

				if (length != 0) {
					for (Integer i = 0; i < length-1; i++) {
						Annotation[] annos = parameters[i];
						if (annos[0] instanceof WxParameters) {
					}
					}
					if (length == 0) {
						throw new RuntimeException(String.format("缺少参数注解", method.getName()));
					}
					if(length == 3) {
						testGlobal.setHeaders((Map<String, ?>) args[2]);
					}
					switch(description) {
					case "parameters":
						testGlobal.setParams((Map<String, ?>) args[0]);
						break;
					case "body":	
						testGlobal.setBody(args[0].toString());
						break;
					}
					testGlobal.setUrl(args[1].toString());
				}
				return HttpUtils.httpRequest(testGlobal);
			}
		});
	}
}
