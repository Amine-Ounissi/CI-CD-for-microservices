package com.value.business.accessmanager.repository;

import com.value.business.accessmanager.domain.Greeting;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreetingsRepository extends CrudRepository<Greeting, String> {

  @Override
  List<Greeting> findAll();
}