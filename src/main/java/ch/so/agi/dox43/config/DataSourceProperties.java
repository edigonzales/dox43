package ch.so.agi.dox43.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class DataSourceProperties {
    private List<DataSourceConfig> datasources;

    public List<DataSourceConfig> getDatasources() {
        return datasources;
    }

    public void setDatasource(List<DataSourceConfig> datasources) {
        this.datasources = datasources;
    }
}
