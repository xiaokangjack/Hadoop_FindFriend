package com.kang.action;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kang.service.DBService;
import com.kang.util.Utils;
import com.opensymphony.xwork2.ActionSupport;

@Component("loginUserAction")
//@component （把普通pojo实例化到spring容器中，相当于配置文件中的<bean id="" class=""/>）
public class LoginUserAction extends ActionSupport {
	/*
	 * 处理登录操作的action
	 */
	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(LoginUserAction.class);
	/*
	 * 使用指定类初始化日志对象
	 * 在日志输出的时候，可以打印出日志信息所在类
	 * 如：Logger logger = LoggerFactory.getLogger(com.Book.class);
	 * logger.debug("日志信息");将会打印出: com.Book : 日志信息
	 */
	@Resource
	private DBService dBService;
	/*
	 * @Autowired默认按类型装配（这个注解是属业spring的），默认情况下必须
	 * 要求依赖对象必须存在，如果要允许null 值，可以设置它的required属性
	 * 为false，如：@Autowired(required=false) 
	 * @Resource（这个注解属于J2EE的），默认安照名称进行装配，名称可以通过name属性进行指定， 
	 * 如果没有指定name属性，当注解写在字段上时，默认取字段名进行按照名称
	 * 查找，如果注解写在setter方法上默认取属性名进行装配。 当找不到与
	 * 名称匹配的bean时才按照类型进行装配。但是需要注意的是，如果name
	 * 属性一旦指定，就只会按照名称进行装配。推荐用 @Resource注解在字段上，
	 * 且这个注解是属于J2EE的，减少了与spring的耦合。最重要的这样代码看起
	 * 就比较优雅。
	 */
	
	private String password;
	private String username;
	
	
	public void login(){
		log.info("User:{} 正在登陆系统...",username);
		try{
			boolean flag = dBService.getLoginUser(username, password);
			Utils.write2PrintWriter(flag);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	/**
	 * @return the dBService
	 */
	public DBService getdBService() {
		return dBService;
	}
	/**
	 * @param dBService the dBService to set
	 */
	public void setdBService(DBService dBService) {
		this.dBService = dBService;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
}
