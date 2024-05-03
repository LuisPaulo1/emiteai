package com.emiteai.relatorio.repository;

import com.emiteai.relatorio.model.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Integer> {
}