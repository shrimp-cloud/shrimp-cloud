package com.wkclz.camunda.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MultiFormatDateDeserializer extends JsonDeserializer<Date> {

    private static final String[] DATE_FORMATS = new String[]{
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd",
        "MM/dd/yyyy",
        "yyyy-MM-dd'T'HH:mm:ss"
    };

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.parse(date);
            } catch (ParseException e) {
                // Try the next format
            }
        }
        throw new IOException("Cannot parse date: " + date);
    }

}