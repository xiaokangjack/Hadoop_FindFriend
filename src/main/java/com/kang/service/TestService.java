/**
 * 
 */
package com.kang.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kang.dao.BaseDAO;
import com.kang.model.HConstants;
import com.kang.model.LoginUser;

/**
 * 测试
 */
@Service("testService")
public class TestService {
	private Logger log = LoggerFactory.getLogger(TestService.class);

	
	@Resource
	private BaseDAO<LoginUser> loginUserDao;
	@Resource
	private BaseDAO<HConstants> hContDao;
	
	/**
	 * 更新测试完成
	 */
	public void updateUser(){
		
	}
	/**
	 * 初始化LoginUser和HConstants表
	 * @return true/false
	 */
	public boolean insertTables(){
		// 清空登录表，并添加用户
		try{
			loginUserDao.executeHql("delete LoginUser"); 
			loginUserDao.save(new LoginUser("admin","admin"));
			loginUserDao.save(new LoginUser("test","test"));
			
			// 清空配置表，并添加常量
			hContDao.executeHql("delete HConstants");
			hContDao.save(new HConstants("mapreduce.app-submission.cross-platform","true","是否跨平台提交任务"));
			hContDao.save(new HConstants("fs.defaultFS","hdfs://sparkproject1:9000","namenode主机及端口"));
			hContDao.save(new HConstants("mapreduce.framework.name","yarn","mapreduce 使用配置"));
			hContDao.save(new HConstants("yarn.resourcemanager.address","sparkproject1:8032","ResourceManager主机及端口"));
			hContDao.save(new HConstants("yarn.resourcemanager.scheduler.address","sparkproject1:8030","Scheduler主机及端口"));
		}catch(Exception e){
			log.info("loginuser表和hconstants表初始化失败\n{}",e.getLocalizedMessage());
			return false;
		}
		
		return true;
	}

	/**
	 * 
	 */
	public void saveUser() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the loginUserDao
	 */
	public BaseDAO<LoginUser> getLoginUserDao() {
		return loginUserDao;
	}

	/**
	 * @param loginUserDao the loginUserDao to set
	 */
	public void setLoginUserDao(BaseDAO<LoginUser> loginUserDao) {
		this.loginUserDao = loginUserDao;
	}

	/**
	 * @return the hContDao
	 */
	public BaseDAO<HConstants> gethContDao() {
		return hContDao;
	}

	/**
	 * @param hContDao the hContDao to set
	 */
	public void sethContDao(BaseDAO<HConstants> hContDao) {
		this.hContDao = hContDao;
	}
	
}
