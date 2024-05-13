package com.emiteai.service.impl;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import com.emiteai.controller.dto.RelatorioPesssoaDto;
import com.emiteai.model.Pessoa;
import com.emiteai.model.Relatorio;
import com.emiteai.publisher.EmiteaiPublisher;
import com.emiteai.repository.PessoaRepository;
import com.emiteai.repository.RelatorioPessoaRepository;
import com.emiteai.service.PessoaService;
import com.emiteai.service.exception.NegocioException;
import com.emiteai.service.exception.RecursoNaoEncontradoException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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

    private final PessoaRepository pessoaRepository;

    private final RelatorioPessoaRepository relatorioPessoaRepository;

    private final ModelMapper modelMapper;

    private final EmiteaiPublisher emiteaiPublisher;

    private SseEmitter sseEmitter;

    @Value("${app-properties.rabbitmq.relatorio.queue}")
    private String relatorio;

    private boolean isEmitterComplete = false;

    private List<RelatorioPesssoaDto> relatorioPessoas = new ArrayList<>();

    public PessoaServiceImpl(PessoaRepository pessoaRepository, RelatorioPessoaRepository relatorioPessoaRepository, ModelMapper modelMapper, EmiteaiPublisher emiteaiPublisher) {
        this.pessoaRepository = pessoaRepository;
        this.relatorioPessoaRepository = relatorioPessoaRepository;
        this.modelMapper = modelMapper;
        this.emiteaiPublisher = emiteaiPublisher;
    }

    @Override
    public Page<PessoaResponseDto> listar(Pageable pageable) {
        Page<Pessoa> pessoas = pessoaRepository.findAll(pageable);
        List<PessoaResponseDto> pessoasDto = modelMapper.map(pessoas.getContent(), List.class);
        return new PageImpl<>(pessoasDto, pageable, pessoas.getTotalElements());
    }

    @Override
    public void solicitarRelatorio() {
        if(this.sseEmitter != null && !this.isEmitterComplete) {
            log.info("Solicitação de emissão de relatório enviado para a fila: {}", relatorio);
            emiteaiPublisher.sendMessage(relatorio, "Gerar relatório de pessoas");
        }
    }

    @Override
    @Transactional
    public String getRelatorio() {
        log.info("Emitindo o relatório para o cliente.");
        List<RelatorioPesssoaDto> relatorioPessoas = new ArrayList<>(this.relatorioPessoas);
        StringBuilder csvBuilder = converterListaRelatorioPessoasParaCsv(relatorioPessoas);
        deletarRelatorio();
        return csvBuilder.toString();
    }

    @Override
    public void setSseEmitter(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
        this.isEmitterComplete = false;
    }

    @Override
    public void buscarRelatorio() {
        log.info("Buscando relatório.");
        List<Relatorio> relatorioPessoas = relatorioPessoaRepository.findAll();
        if (relatorioPessoas.isEmpty()) {
            log.info("Relatório não encontrado.");
            return;
        }
        this.relatorioPessoas = converterParaRelatorioPessoaDto(relatorioPessoas);
        if (this.sseEmitter != null && !this.isEmitterComplete) {
            try {
                this.sseEmitter.send("CONCLUIDO");
                this.sseEmitter.complete();
                this.isEmitterComplete = true;
                log.info("Relatório CONCLUÍDO.");
            } catch (IOException e) {
                this.sseEmitter.completeWithError(e);
                this.isEmitterComplete = true;
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
        boolean isCpfDuplicado = isCpfDuplicado(null, pessoaRequestDto);
        if (!isCpfDuplicado) {
            throw new NegocioException("CPF já cadastrado.");
        }
        Pessoa pessoa = new Pessoa();
        modelMapper.map(pessoaRequestDto, pessoa);
        pessoa = pessoaRepository.save(pessoa);
        return modelMapper.map(pessoa, PessoaResponseDto.class);
    }

    @Override
    @Transactional
    public PessoaResponseDto atualizar(Integer id, PessoaRequestDto pessoaRequestDto) {
        Pessoa pessoa = buscarPessoaPorId(id);
        boolean isCpfDuplicado = isCpfDuplicado(id, pessoaRequestDto);
        if (!isCpfDuplicado) {
            throw new NegocioException("CPF já cadastrado.");
        }
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

    private boolean isCpfDuplicado(Integer id, PessoaRequestDto pessoaRequestDto) {
        String cpf = pessoaRequestDto.getCpf();
        log.info("Validando se o CPF {} está cadastrado na base de dados...", cpf);
        Pessoa pessoa = pessoaRepository.findByCpf(cpf);
        if (pessoa != null && id != null) {
            Pessoa pessoaAtual = pessoaRepository.findById(id).orElse(null);
            if (pessoaAtual != null) {
                return pessoaAtual.getId().equals(pessoa.getId());
            }
        }
        return pessoa == null;
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

    private void deletarRelatorio() {
        log.info("Deletando relatório...");
        relatorioPessoas.clear();
        relatorioPessoaRepository.deleteAll();
    }
}
