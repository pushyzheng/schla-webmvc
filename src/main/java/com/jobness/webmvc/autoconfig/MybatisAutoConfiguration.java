package com.jobness.webmvc.autoconfig;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author Pushy
 * @since 2019/3/8 11:26
 */
@Configuration
public class MybatisAutoConfiguration {

    @Bean
    public DruidDataSource dataSource() {
        String url = AutoConfigRegistry.reader.getDatasourceUrl();
        String username = AutoConfigRegistry.reader.getDatasourceUsername();
        String password = AutoConfigRegistry.reader.getDatasourcePassword();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(5);
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        String mapperLocations = AutoConfigRegistry.reader.getMapperLocation();

        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        return sessionFactory.getObject();
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        String mapperPackage = AutoConfigRegistry.reader.getMapperPackage();

        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        scannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        scannerConfigurer.setBasePackage(mapperPackage);
        return scannerConfigurer;
    }



}
