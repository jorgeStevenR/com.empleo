package com.portalempleos.repository;

import com.portalempleos.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByNit(String nit);

    @Query("SELECT c FROM Company c WHERE c.emailEntity.email = :email")
    Optional<Company> findByEmail(@Param("email") String email);

}
