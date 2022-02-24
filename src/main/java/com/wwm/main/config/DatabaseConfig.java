package com.wwm.main.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "spring")
public class DatabaseConfig {
	
	public List<DBSetting> datasource = new ArrayList<DBSetting>();
	
	static class DBSetting extends HikariConfig{
		
		@Getter
		@Setter
		String name;
		
		@Getter
		@Setter
		String mapperLocations;
	}
	
	
	public List<DBSetting> getDatasource(){
		return datasource;
	}
	
	@Bean(initMethod = "init", destroyMethod = "destroy")
	public MultiDataSource multiDataSource() {
		return new MultiDataSource(datasource);
	}
}
