package com.portalempleos.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonHibernateConfig {
  @Bean
  public Hibernate6Module hibernateModule() {
    Hibernate6Module m = new Hibernate6Module();
    // Serializa identificadores si la asociación LAZY no está cargada
    m.enable(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
    return m;
  }
}
