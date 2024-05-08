package com.emiteai.repository;

import com.emiteai.model.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioPessoaRepository extends JpaRepository<Relatorio, Integer> {
}
