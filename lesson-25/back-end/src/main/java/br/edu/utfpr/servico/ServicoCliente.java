package br.edu.utfpr.servico;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.edu.utfpr.dto.ClienteDTO;
import br.edu.utfpr.dto.PaisDTO;
import br.edu.utfpr.excecao.NomeClienteMenor5CaracteresException;

@RestController
public class ServicoCliente {
    private List<PaisDTO> paises;
    private List<ClienteDTO> clientes;

    public ServicoCliente() {
        paises = Stream.of(
            PaisDTO.builder().id(1).nome("Brasil").sigla("BR").codigoTelefone(55).build(),
            PaisDTO.builder().id(2).nome("Estados Unidos da América").sigla("EUA").codigoTelefone(33).build(),
            PaisDTO.builder().id(3).nome("Reino Unido").sigla("RU").codigoTelefone(44).build()
        ).collect(Collectors.toList());

        clientes = Stream.of(
            ClienteDTO.builder().id(1).nome("Joao").idade(23).telefone("99999-9999").limiteCredito(2500.00).pais(paises.get(0)).build(),
            ClienteDTO.builder().id(1).nome("Alice").idade(27).telefone("22222-2222").limiteCredito(3700.00).pais(
                paises.get(1)).build(),
            ClienteDTO.builder().id(1).nome("Kim").idade(33).telefone("55555-5555").limiteCredito(7800.00).pais(
                paises.get(2)).build()
        ).collect(Collectors.toList());
    }

    private int id;
    private String nome;
    private int idade;
    private String telefone;
    private double limiteCredito;
    private PaisDTO pais;

    @GetMapping("/servico/cliente")
    public ResponseEntity<List<ClienteDTO>> listar() {
        // public List<ClienteDTO> listar() {
        // return clientes;
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/servico/cliente/{id}")
    public ResponseEntity<ClienteDTO> listarPorId(@PathVariable int id) {
        Optional<ClienteDTO> clienteEncontrado = clientes.stream().filter(p -> p.getId() == id).findAny();

        return ResponseEntity.of(clienteEncontrado);
    }

    @PostMapping("/servico/cliente")
    public ResponseEntity<ClienteDTO> criar(@RequestBody ClienteDTO cliente) {

        cliente.setId(clientes.size() + 1);
        clientes.add(cliente);

        return ResponseEntity.status(201).body(cliente);
    }

    @DeleteMapping("/servico/cliente/{id}")
    public ResponseEntity excluir(@PathVariable int id) {

        if (clientes.removeIf(cliente -> cliente.getId() == id))
            return ResponseEntity.noContent().build();

        else
            return ResponseEntity.notFound().build();
    }

    @PutMapping("/servico/cliente/{id}")
    public ResponseEntity<ClienteDTO> alterar(@PathVariable int id, @RequestBody ClienteDTO cliente) {
        Optional<ClienteDTO> clienteExistente = clientes.stream().filter(c -> c.getId() == id).findAny();

        clienteExistente.ifPresent(c -> {
            try {
                c.setNome(cliente.getNome());
            } catch (NomeClienteMenor5CaracteresException ex) {
                ex.printStackTrace();
            }
            c.setIdade(cliente.getIdade());
            c.setTelefone(cliente.getTelefone());
            c.setLimiteCredito(cliente.getLimiteCredito());
            c.setPais(paises.stream().filter(p -> p.getId() == cliente.getPais().getId()).findAny().get());
        });

        return ResponseEntity.of(clienteExistente);
    }
}