package com.nix.api.rest.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateDeserializer extends JsonDeserializer<Date> {

    private static final Logger log = LoggerFactory.getLogger(DateDeserializer.class);

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        Date birthday;
        try {
            birthday = dateFormat.parse(p.getValueAsString("birthday"));
        } catch (ParseException e) {
            log.error("cannot parse date: {}", p.getValueAsString("birthday"));
            birthday = null;
        }
        return birthday;
    }
}
