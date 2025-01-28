package com.login.login_page.repository;

import com.login.login_page.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByUsername(String userName);
}