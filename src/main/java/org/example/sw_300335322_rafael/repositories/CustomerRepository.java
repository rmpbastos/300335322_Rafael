package org.example.sw_300335322_rafael.repositories;

import org.example.sw_300335322_rafael.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
