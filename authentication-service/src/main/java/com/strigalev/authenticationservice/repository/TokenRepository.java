package com.strigalev.authenticationservice.repository;

import com.strigalev.authenticationservice.jwt.RefreshToken;
import org.springframework.data.repository.CrudRepository;


public interface TokenRepository extends CrudRepository<RefreshToken, Long> {

}
