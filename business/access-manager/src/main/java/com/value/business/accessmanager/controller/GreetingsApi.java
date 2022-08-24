package com.value.business.accessmanager.controller;

import com.value.business.accessmanager.domain.Greeting;
import com.value.business.accessmanager.mapper.GreetingsMapper;
import com.value.business.accessmanager.model.Message;
import com.value.business.accessmanager.service.GreetingsService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/v1/greetings")
public class GreetingsApi {

  @Autowired
  private GreetingsService greetingsService;

  @RequestMapping(method = RequestMethod.GET, value = "/message/{id}", produces = {
    "application/json"
  })
  @ResponseStatus(HttpStatus.OK)
  public Message getMessage(@PathVariable(name = "id") String id) {
    Greeting greeting = greetingsService.getGreetingById(id);
    return GreetingsMapper.INSTANCE.greetingToMessage(greeting);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/messages", produces = {
    "application/json"
  })
  @ResponseStatus(HttpStatus.OK)
  public List<Message> getMessages() {
    List<Greeting> greetings = greetingsService.getGreetings();
    return GreetingsMapper.INSTANCE.greetingsToMessages(greetings);
  }
}
