package com.kang.dao;

import java.io.Serializable;  
import java.math.BigInteger;
import java.util.List;  

import javax.transaction.Transaction;
import javax.transaction.Transactional;
  
import org.hibernate.Query;  
import org.hibernate.Session;  
import org.hibernate.SessionFactory;  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Repository;

import com.kang.util.Utils;
//为什么 @Repository 只能标注在 DAO 类上呢？
//这是因为该注解的作用不只是将类识别为Bean，
//同时它还能将所标注的类中抛出的数据访问异常封装为 Spring
//的数据访问异常类型。 Spring本身提供了一个丰富的并且是
//与具体的数据访问技术无关的数据访问异常结构，用于封装不同的持久层框架抛出的异常，
//使得异常独立于底层的框架。
//@Repository为数据层dao的类注解 
@Repository("baseDAO")  
@SuppressWarnings("all")  
public class BaseDAOImpl<T> implements BaseDAO<T> {  
	private Logger log = LoggerFactory.getLogger(BaseDAOImpl.class);
    private SessionFactory sessionFactory;  
  
    public SessionFactory getSessionFactory() {  
        return sessionFactory;  
    }  
  
    @Autowired  
    public void setSessionFactory(SessionFactory sessionFactory) {  
        this.sessionFactory = sessionFactory;  
    }  
  
    private Session getCurrentSession() {  
        return sessionFactory.getCurrentSession();  
    }  
  
    public Serializable save(T o) {  
        return this.getCurrentSession().save(o);  
    }  
  
    public void delete(T o) {  
        this.getCurrentSession().delete(o);  
    }  
  
    public void update(T o) {  
        this.getCurrentSession().update(o);  
    }  
  
    public void saveOrUpdate(T o) {  
        this.getCurrentSession().saveOrUpdate(o);  
    }  
  
    public List<T> find(String hql) {  
        return this.getCurrentSession().createQuery(hql).list();  
    }  
  
    public List<T> find(String hql, Object[] param) {  
        Query q = this.getCurrentSession().createQuery(hql);  
        if (param != null && param.length > 0) {  
            for (int i = 0; i < param.length; i++) {  
                q.setParameter(i, param[i]);  
            }  
        }  
        return q.list();  
    }  
  
    public List<T> find(String hql, List<Object> param) {  
        Query q = this.getCurrentSession().createQuery(hql);  
        if (param != null && param.size() > 0) {  
            for (int i = 0; i < param.size(); i++) {  
                q.setParameter(i, param.get(i));  
            }  
        }  
        return q.list();  
    }  
  
    public List<T> find(String hql, Object[] param, Integer page, Integer rows) {  
        if (page == null || page < 1) {  
            page = 1;  
        }  
        if (rows == null || rows < 1) {  
            rows = 10;  
        }  
        Query q = this.getCurrentSession().createQuery(hql);  
        if (param != null && param.length > 0) {  
            for (int i = 0; i < param.length; i++) {  
                q.setParameter(i, param[i]);  
            }  
        }  
        return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();  
    }  
  
    public List<T> find(String hql, List<Object> param, Integer page, Integer rows) {  
        if (page == null || page < 1) {  
            page = 1;  
        }  
        if (rows == null || rows < 1) {  
            rows = 10;  
        }  
        Query q = this.getCurrentSession().createQuery(hql);  
        if (param != null && param.size() > 0) {  
            for (int i = 0; i < param.size(); i++) {  
                q.setParameter(i, param.get(i));  
            }  
        }  
        return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();  
    }  
  
    public T get(Class<T> c, Serializable id) {  
        return (T) this.getCurrentSession().get(c, id);  
    }  
  
    public T get(String hql, Object[] param) {  
        List<T> l = this.find(hql, param);  
        if (l != null && l.size() > 0) {  
            return l.get(0);  
        } else {  
            return null;  
        }  
    }  
  
    public T get(String hql, List<Object> param) {  
        List<T> l = this.find(hql, param);  
        if (l != null && l.size() > 0) {  
            return l.get(0);  
        } else {  
            return null;  
        }  
    }  
  
    public Long count(String hql) {  
        return (Long) this.getCurrentSession().createQuery(hql).uniqueResult();  
    }  
    
    public BigInteger sqlCount(String sql){
    	return (BigInteger)this.getCurrentSession().createSQLQuery(sql).uniqueResult();
    }
  
    public Long count(String hql, Object[] param) {  
        Query q = this.getCurrentSession().createQuery(hql);  
        if (param != null && param.length > 0) {  
            for (int i = 0; i < param.length; i++) {  
                q.setParameter(i, param[i]);  
            }  
        }  
        return (Long) q.uniqueResult();  
    }  
  
    public Long count(String hql, List<Object> param) {  
        Query q = this.getCurrentSession().createQuery(hql);  
        if (param != null && param.size() > 0) {  
            for (int i = 0; i < param.size(); i++) {  
                q.setParameter(i, param.get(i));  
            }  
        }  
        return (Long) q.uniqueResult();  
    }  
  
    public Integer executeHql(String hql) {  
        return this.getCurrentSession().createQuery(hql).executeUpdate();  
    }  
  
    public Integer executeHql(String hql, Object[] param) {  
        Query q = this.getCurrentSession().createQuery(hql);  
        if (param != null && param.length > 0) {  
            for (int i = 0; i < param.length; i++) {  
                q.setParameter(i, param[i]);  
            }  
        }  
        return q.executeUpdate();  
    }  
  
    public Integer executeHql(String hql, List<Object> param) {  
        Query q = this.getCurrentSession().createQuery(hql);  
        if (param != null && param.size() > 0) {  
            for (int i = 0; i < param.size(); i++) {  
                q.setParameter(i, param.get(i));  
            }  
        }  
        return q.executeUpdate();  
    }

	@Override
	public Integer saveBatch(List<Object> lists) {
		Session session = this.getCurrentSession();
//		org.hibernate.Transaction tx = session.beginTransaction();
		int i=0;
		try{
		for ( Object l:lists) {
			i++;
		    session.save(l);
			if( i % 50 == 0 ) { // Same as the JDBC batch size
		        //flush a batch of inserts and release memory:
		        session.flush();
		        session.clear();
		        if(i%1000==0){
		        	System.out.println(new java.util.Date()+"：已经预插入了"+i+"条记录...");
		        }
		    }
		}}catch(Exception e){
			e.printStackTrace();
		}
//		tx.commit();
//		session.close();
		Utils.simpleLog("插入数据数为："+i);
		return i;
	}

  
}  
