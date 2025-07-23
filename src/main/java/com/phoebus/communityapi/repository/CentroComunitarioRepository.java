package com.phoebus.communityapi.repository;

import com.phoebus.communityapi.model.CentroComunitario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CentroComunitarioRepository extends MongoRepository<CentroComunitario, String> {
}