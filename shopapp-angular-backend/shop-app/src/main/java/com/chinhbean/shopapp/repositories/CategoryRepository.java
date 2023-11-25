package com.chinhbean.shopapp.repositories;

import com.chinhbean.shopapp.models.Category;
import com.chinhbean.shopapp.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
