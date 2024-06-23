package com.emiteai.config;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

@JsonComponent
public class PageJsonSerializer extends JsonSerializer<Page<?>> {

	@Override
	public void serialize(Page<?> page, JsonGenerator gen,
			SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeObjectField("content", page.getContent());
		gen.writeNumberField("totalPages", page.getTotalPages());
		gen.writeNumberField("totalElements", page.getTotalElements());
		gen.writeNumberField("size", page.getSize());
		gen.writeNumberField("number", page.getNumber());
		gen.writeBooleanField("last", page.isLast());
		gen.writeEndObject();
	}

}