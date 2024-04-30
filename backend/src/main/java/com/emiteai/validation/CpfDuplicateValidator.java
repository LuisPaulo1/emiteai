package com.emiteai.validation;

import com.emiteai.model.Pessoa;
import com.emiteai.repository.PessoaRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CpfDuplicateValidator implements ConstraintValidator<CpfDuplicateValid, String> {

	@Autowired
	private PessoaRepository repository;

	@Override
	public boolean isValid(String cpf, ConstraintValidatorContext context) {
		Pessoa pessoa = repository.findByCpf(cpf);
		return pessoa == null;
	}
}