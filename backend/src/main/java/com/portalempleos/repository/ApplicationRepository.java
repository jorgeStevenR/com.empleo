package com.portalempleos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.portalempleos.model.Application;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJob_CompanyEntity_IdCompany(Long idCompany);
}
