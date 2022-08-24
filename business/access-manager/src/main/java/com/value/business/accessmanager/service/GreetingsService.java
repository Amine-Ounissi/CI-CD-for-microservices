package com.value.business.accessmanager.service;

import com.value.business.accessmanager.domain.Greeting;
import java.util.List;

public interface GreetingsService {

  List<Greeting> getGreetings();

  Greeting getGreetingById(String id);

  void addNewGreeting(Greeting greeting);
}