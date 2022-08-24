package com.value.business.accessmanager.service;

import com.value.buildingblocks.presentation.errors.NotFoundException;
import com.value.business.accessmanager.domain.Greeting;
import com.value.business.accessmanager.repository.GreetingsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GreetingsServiceImpl implements GreetingsService {

  @Autowired
  private GreetingsRepository greetingsRepository;

  @Override
  public List<Greeting> getGreetings() {
    return greetingsRepository.findAll();
  }

  @Override
  public Greeting getGreetingById(String id) {
    return greetingsRepository.findById(id).orElseThrow(() -> {
      throw new NotFoundException(String.format("Greeting with id: %s is not found", id));
    });
  }

  @Override
  public void addNewGreeting(Greeting greeting) {
    greetingsRepository.save(greeting);
  }
}