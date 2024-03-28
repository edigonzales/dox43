package ch.so.agi.dox43;

import java.lang.reflect.InvocationTargetException;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;

import ch.so.agi.dox43.config.DataSourceConfig;
import ch.so.agi.dox43.config.DataSourceProperties;
import jakarta.annotation.PostConstruct;

@Component
public class JdbcTemplateFactory {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataSourceProperties dataSourceProperties;
    
    private Map<String, NamedParameterJdbcTemplate> jdbcTemplates = new HashMap<>();
    
    public JdbcTemplateFactory(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }
    
    @PostConstruct
    private void createClients() throws Exception {        
        for (DataSourceConfig dsc :  dataSourceProperties.getDatasources()) {
            // Schauen, wie sich diese SimpleDriverDataSource verhält. 
            // Ist kein Connection Pool!            
            Driver driver = (Driver)Class.forName(dsc.getDriverClassName()).getDeclaredConstructor().newInstance();
            
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            dataSource.setDriver(driver);
            dataSource.setUrl(dsc.getUrl());
            dataSource.setUsername(dsc.getUsername());
            dataSource.setPassword(dsc.getPassword());
         
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            jdbcTemplates.put(dsc.getKey(), jdbcTemplate);
        }
    }
    
    public NamedParameterJdbcTemplate getClient(String key) {
        return jdbcTemplates.get(key);
    }
}
