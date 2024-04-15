package com.project.userrole.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.userrole.entity.Product;
public interface productRepo extends JpaRepository<Product, Integer> {

}
