package com.dak.duty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Organisation;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

}
