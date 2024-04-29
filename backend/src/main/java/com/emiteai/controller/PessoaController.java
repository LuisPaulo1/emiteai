package com.emiteai.controller;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import com.emiteai.service.PessoaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(path = "/v1/pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public ResponseEntity<List<PessoaResponseDto>> findAll() {
        List<PessoaResponseDto> pessoas = pessoaService.listar();
        return ResponseEntity.ok(pessoas);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PessoaResponseDto> findById(@PathVariable Integer id) {
        PessoaResponseDto pessoa = pessoaService.buscar(id);
        return ResponseEntity.ok(pessoa);
    }

    @PostMapping
    public ResponseEntity<PessoaResponseDto> save(@RequestBody @Valid PessoaRequestDto pessoaRequestDto) {
        PessoaResponseDto pessoa = pessoaService.salvar(pessoaRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoa);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<PessoaResponseDto> update(@PathVariable Integer id, @RequestBody @Valid PessoaRequestDto pessoaRequestDto) {
        PessoaResponseDto pessoa = pessoaService.atualizar(id, pessoaRequestDto);
        return ResponseEntity.ok(pessoa);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        pessoaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
