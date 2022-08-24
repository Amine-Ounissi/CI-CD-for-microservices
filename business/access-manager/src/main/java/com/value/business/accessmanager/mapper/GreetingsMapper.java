package com.value.business.accessmanager.mapper;

import com.value.business.accessmanager.domain.Greeting;
import com.value.business.accessmanager.model.Message;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GreetingsMapper {

  GreetingsMapper INSTANCE = Mappers.getMapper(GreetingsMapper.class);

  Message greetingToMessage(Greeting greeting);

  List<Message> greetingsToMessages(List<Greeting> greetings);

  Greeting messageToGreeting(Message message);
}