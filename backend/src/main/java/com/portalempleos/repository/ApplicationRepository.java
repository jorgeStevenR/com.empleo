package com.portalempleos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.portalempleos.model.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
