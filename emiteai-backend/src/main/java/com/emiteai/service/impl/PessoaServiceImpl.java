package com.emiteai.service.impl;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import com.emiteai.controller.dto.RelatorioPesssoaDto;
import com.emiteai.model.Pessoa;
import com.emiteai.model.Relatorio;
import com.emiteai.publisher.EmiteaiPublisher;
import com.emiteai.repository.PessoaRepository;
import com.emiteai.repository.RelatorioRepository;
import com.emiteai.service.PessoaService;
import com.emiteai.service.exception.RecursoNaoEncontradoException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class PessoaServiceImpl implements PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmiteaiPublisher emiteaiPublisher;

    private SseEmitter sseEmitter;

    @Value("${app-properties.rabbitmq.relatorio.queue}")
    private String relatorioQueue;

    @Getter
    private String relatorioStatus = "EM_PROCESSAMENTO";

    private List<RelatorioPesssoaDto> relatorioPessoas = new ArrayList<>();

    @Override
    public Page<PessoaResponseDto> listar(Pageable pageable) {
        Page<Pessoa> pessoas = pessoaRepository.findAll(pageable);
        List<PessoaResponseDto> pessoasDto = modelMapper.map(pessoas.getContent(), List.class);
        return new PageImpl<>(pessoasDto, pageable, pessoas.getTotalElements());
    }

    @Override
    public void solicitarRelatorio() {
        emiteaiPublisher.sendMessage(relatorioQueue, "Solicitando emissão de relatório");
    }

    @Override
    @Transactional
    public String getRelatorio() {
        log.info("Emitindo o relatório para o cliente...");
        List<RelatorioPesssoaDto> relatorioPessoas = new ArrayList<>(this.relatorioPessoas);
        StringBuilder csvBuilder = converterListaRelatorioPessoasParaCsv(relatorioPessoas);
        deletarRelatorio();
        return csvBuilder.toString();
    }

    @Override
    public void setSseEmitter(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    @Override
    public void buscarRelatorio() {
        log.info("Emiteai notificado, buscando relatório...");
        List<Relatorio> relatorioPessoas = relatorioRepository.findAll();
        this.relatorioPessoas = converterParaRelatorioPessoaDto(relatorioPessoas);
        this.relatorioStatus = "CONCLUIDO";
        if (this.sseEmitter != null) {
            try {
                this.sseEmitter.send(this.getRelatorioStatus());
                this.sseEmitter.complete();
                log.info("Relatório CONCLUÍDO");
            } catch (IOException e) {
                this.sseEmitter.completeWithError(e);
            }
        }
    }

    @Override
    public PessoaResponseDto buscar(Integer id) {
        Pessoa pessoa = buscarPessoaPorId(id);
        return modelMapper.map(pessoa, PessoaResponseDto.class);
    }

    @Override
    @Transactional
    public PessoaResponseDto cadastrar(PessoaRequestDto pessoaRequestDto) {
        Pessoa pessoa = new Pessoa();
        modelMapper.map(pessoaRequestDto, pessoa);
        pessoa = pessoaRepository.save(pessoa);
        return modelMapper.map(pessoa, PessoaResponseDto.class);
    }

    @Override
    @Transactional
    public PessoaResponseDto atualizar(Integer id, PessoaRequestDto pessoaRequestDto) {
        Pessoa pessoa = buscarPessoaPorId(id);
        modelMapper.map(pessoaRequestDto, pessoa);
        pessoa = pessoaRepository.save(pessoa);
        return modelMapper.map(pessoa, PessoaResponseDto.class);
    }

    @Override
    @Transactional
    public void deletarPorId(Integer id) {
        Pessoa pessoa = pessoaRepository.findById(id).orElse(null);
        if (pessoa == null) {
            throw new RecursoNaoEncontradoException(String.format("Pessoa com id %d não encontrada.", id));
        }
        pessoaRepository.delete(pessoa);
    }

    private StringBuilder converterListaRelatorioPessoasParaCsv(List<RelatorioPesssoaDto> relatorioPessoas) {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("id").append(",");
        csvBuilder.append("nome").append(",");
        csvBuilder.append("telefone").append(",");
        csvBuilder.append("cpf").append(",");
        csvBuilder.append("numero").append(",");
        csvBuilder.append("complemento").append(",");
        csvBuilder.append("cep").append(",");
        csvBuilder.append("bairro").append(",");
        csvBuilder.append("municipio").append(",");
        csvBuilder.append("estado").append("\n");
        for (RelatorioPesssoaDto item : relatorioPessoas) {
            csvBuilder.append(StringUtils.arrayToCommaDelimitedString(new Object[] {
                    item.getId(),
                    item.getNome(),
                    item.getTelefone(),
                    item.getCpf(),
                    item.getNumero(),
                    item.getComplemento(),
                    item.getCep(),
                    item.getBairro(),
                    item.getMunicipio(),
                    item.getEstado()
            })).append("\n");
        }
        return csvBuilder;
    }

    private List<RelatorioPesssoaDto> converterParaRelatorioPessoaDto(List<Relatorio> relatorioPessoas) {
        List<RelatorioPesssoaDto> listaRelatorioPessoa = new ArrayList<>();
       relatorioPessoas.forEach(relatorio -> {
           RelatorioPesssoaDto relatorioPesssoaDto = new RelatorioPesssoaDto();
           relatorioPesssoaDto.setId(relatorio.getId());
           relatorioPesssoaDto.setNome(relatorio.getNome());
           relatorioPesssoaDto.setTelefone(relatorio.getTelefone());
           relatorioPesssoaDto.setCpf(relatorio.getCpf());
           relatorioPesssoaDto.setNumero(relatorio.getNumero());
           relatorioPesssoaDto.setComplemento(relatorio.getComplemento());
           relatorioPesssoaDto.setCep(relatorio.getCep());
           relatorioPesssoaDto.setBairro(relatorio.getBairro());
           relatorioPesssoaDto.setMunicipio(relatorio.getMunicipio());
           relatorioPesssoaDto.setEstado(relatorio.getEstado());
           listaRelatorioPessoa.add(relatorioPesssoaDto);
       });
       return listaRelatorioPessoa;
    }

    private Pessoa buscarPessoaPorId(Integer id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(String.format("Pessoa com id %d não encontrada.", id)));
    }

    private void deletarRelatorio(){
        log.info("Deletando relatório...");
        relatorioPessoas.clear();
        relatorioRepository.deleteAll();
    }
}
