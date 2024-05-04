package com.emiteai.controller;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import com.emiteai.controller.dto.RelatorioDto;
import com.emiteai.service.PessoaService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Log4j2
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

    @GetMapping(path = "/relatorio")
    public ResponseEntity<List<RelatorioDto>> getRelatorio() {
        log.info("Recebendo requisição para emitir o relatório");
        List<RelatorioDto> relatorio = pessoaService.getRelatorio();
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping(path = "/relatorio/solicitar")
    public ResponseEntity<Void> solicitarEmissaoRelatorio() {
        pessoaService.solicitarRelatorio();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/relatorio/status")
    public SseEmitter getRelatorioStatus() {
        SseEmitter emitter = new SseEmitter();
        pessoaService.setSseEmitter(emitter);
        return emitter;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PessoaResponseDto> findById(@PathVariable Integer id) {
        PessoaResponseDto pessoa = pessoaService.buscar(id);
        return ResponseEntity.ok(pessoa);
    }

    @PostMapping
    public ResponseEntity<PessoaResponseDto> save(@RequestBody @Valid PessoaRequestDto pessoaRequestDto) {
        log.info("Recebendo requisição para cadastrar pessoa: {}", pessoaRequestDto);
        PessoaResponseDto pessoa = pessoaService.cadastrar(pessoaRequestDto);
        log.info("Pessoa salva com sucesso: {}", pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoa);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<PessoaResponseDto> update(@PathVariable Integer id, @RequestBody @Valid PessoaRequestDto pessoaRequestDto) {
        log.info("Recebendo requisição para atualizar pessoa com id {}: {}", id, pessoaRequestDto);
        PessoaResponseDto pessoa = pessoaService.atualizar(id, pessoaRequestDto);
        log.info("Pessoa atualizada com sucesso: {}", pessoa);
        return ResponseEntity.ok(pessoa);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        log.info("Recebendo requisição para deletar pessoa com id {}", id);
        pessoaService.deletarPorId(id);
        log.info("Pessoa deletada com sucesso: {}", id);
        return ResponseEntity.noContent().build();
    }
}
