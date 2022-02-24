package com.wwm.main.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.wwm.main.config.DatabaseConfig.DBSetting;
import com.zaxxer.hikari.HikariDataSource;

public class MultiDataSource {
	private static final Logger logger = LoggerFactory.getLogger(MultiDataSource.class);
	
	List<HikariDataSource> dataSourceList = new ArrayList<HikariDataSource>();
	Map<String, SqlSessionTemplate> sessionTemplateMap = new HashMap<String, SqlSessionTemplate>();
	Map<String, DataSourceTransactionManager> txManagerMap = new HashMap<String, DataSourceTransactionManager>();
	List<DBSetting> settings;
	
	
	public MultiDataSource (List<DBSetting> settings) {
		logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		this.settings = settings;
	}
	
	public void init() throws Exception{
		logger.info("=====================================");
		logger.info("DATABASE SETTING START");
		logger.info("=====================================");
		
		for (DBSetting setting : settings) {
			logger.info("Name : " + setting.name);
			logger.info("DriverClassName : " + setting.getDriverClassName());
			logger.info("JdbcUrl : " + setting.getJdbcUrl());
			logger.info("Max : " + setting.getMaximumPoolSize());
			HikariDataSource dataSource = new HikariDataSource(setting);
			dataSourceList.add(dataSource);
			
			SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
			sqlSessionFactoryBean.setDataSource(dataSource);
			
			ClassLoader cl = this.getClass().getClassLoader();
			ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
			sqlSessionFactoryBean.setConfigLocation(resolver.getResource("classpath:mybatis-config.xml"));
			Resource resource[] = resolver.getResources(setting.getMapperLocations());
			sqlSessionFactoryBean.setMapperLocations(resource);
			
			DataSourceTransactionManager dt = new DataSourceTransactionManager(dataSource);
			sessionTemplateMap.put(setting.getName(), new SqlSessionTemplate(sqlSessionFactoryBean.getObject()));
			txManagerMap.put(setting.getName(), dt);
		}
		logger.info("=====================================");
		logger.info("DATABASE SETTING END");
		logger.info("=====================================");
	}
	public SqlSessionTemplate getSessionTemplate(String name) {
		return sessionTemplateMap.get(name);
	}
	
	public DataSourceTransactionManager getDataSourceTransactionManager(String name) {
		return txManagerMap.get(name);
	}
	
	public void destroy() {
		logger.info("====================================");
		logger.info("DESTROY");
		sessionTemplateMap.clear();
		for (HikariDataSource dataSource : dataSourceList) {
			logger.info(""+dataSource.isClosed());
			if(!dataSource.isClosed()) {
				dataSource.isClosed();
			}
		}
		dataSourceList.clear();
		logger.info("====================================");
	}
	
}
