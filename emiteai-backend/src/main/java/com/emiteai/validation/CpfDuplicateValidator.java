package com.emiteai.validation;

import com.emiteai.model.Pessoa;
import com.emiteai.repository.PessoaRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class CpfDuplicateValidator implements ConstraintValidator<CpfDuplicateValid, String> {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private PessoaRepository repository;

	@Override
	public boolean isValid(String cpf, ConstraintValidatorContext context) {
		log.info("Validando se o CPF {} está cadastrado na base de dados...", cpf);
		Pessoa pessoa = repository.findByCpf(cpf);
		if (pessoa != null && request.getMethod().equals("PUT")) {
			Integer id = Integer.parseInt(request.getRequestURI().split("/")[3]);
			Pessoa pessoaAtual = repository.findById(id).orElse(null);
			if (pessoaAtual != null) {
				return pessoaAtual.getId().equals(pessoa.getId());
			}
		}
		return pessoa == null;
	}
}