package com.value.business.accessmanager.mapper;

import com.value.business.accessmanager.domain.Greeting;
import com.value.business.accessmanager.model.Message;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-08-18T12:56:43+0000",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.16 (Ubuntu)"
)
public class GreetingsMapperImpl implements GreetingsMapper {

    @Override
    public Message greetingToMessage(Greeting greeting) {
        if ( greeting == null ) {
            return null;
        }

        Message message = new Message();

        message.setId( greeting.getId() );
        message.setMessage( greeting.getMessage() );

        return message;
    }

    @Override
    public List<Message> greetingsToMessages(List<Greeting> greetings) {
        if ( greetings == null ) {
            return null;
        }

        List<Message> list = new ArrayList<Message>( greetings.size() );
        for ( Greeting greeting : greetings ) {
            list.add( greetingToMessage( greeting ) );
        }

        return list;
    }

    @Override
    public Greeting messageToGreeting(Message message) {
        if ( message == null ) {
            return null;
        }

        Greeting greeting = new Greeting();

        greeting.setId( message.getId() );
        greeting.setMessage( message.getMessage() );

        return greeting;
    }
}
