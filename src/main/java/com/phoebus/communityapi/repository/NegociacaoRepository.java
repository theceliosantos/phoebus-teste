package com.phoebus.communityapi.repository;

import com.phoebus.communityapi.model.Negociacao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NegociacaoRepository extends MongoRepository<Negociacao, String> {
}