package com.emiteai.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CpfDuplicateValidator.class)
public @interface CpfDuplicateValid {
	String message() default "CPF já cadastrado";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}