package br.edu.ufersa.poo.pizzaria.model.services;

import br.edu.ufersa.poo.pizzaria.DAO.ClienteDAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    // regras de cadastro do cliente
    public void cadastrarCliente(String nome, String endereco, String cpf, String telefone, String bairro) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro no cadastro: Precisa ter nome!");
        }
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro no cadastro: Precisa ter endereço.");
        }
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro no cadastro: Precisa ter bairro.");
        }
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro no cadastro: CPF não pode estar vazio.");
        }
        if (telefone == null || telefone.trim().length() < 11) {
            throw new IllegalArgumentException("Erro no cadastro: Telefone inválido (Mínimo 11 caracteres com DDD).");
        }

        // O construtor chama setCpf() que valida tamanho e formato
        Cliente novoCliente = new Cliente(nome, endereco, cpf, telefone, bairro);
        clienteDAO.salvar(novoCliente);
    }

    // regras para atualizar dados de um cliente
    public void atualizarCliente(Cliente clienteAlterado) {
        if (clienteAlterado == null || clienteAlterado.getIdCliente() <= 0) {
            throw new IllegalArgumentException("Erro para atualização: Cliente inválido ou ID não informado.");
        }

        Cliente clienteExistente = clienteDAO.buscarPorId(clienteAlterado.getIdCliente());
        if (clienteExistente == null) {
            throw new IllegalArgumentException("Erro na atualização: Cliente não encontrado no sistema.");
        }

        if (clienteAlterado.getNome() == null || clienteAlterado.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na atualização: O nome não pode ficar vazio.");
        }
        if (clienteAlterado.getEndereco() == null || clienteAlterado.getEndereco().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na atualização: O endereço não pode ficar vazio.");
        }

        //  valida CPF e telefone
        if (clienteAlterado.getCpf() == null || clienteAlterado.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na atualização: O CPF não pode ficar vazio.");
        }
        String cpfLimpo = clienteAlterado.getCpf().replaceAll("[.\\-]", "");
        if (cpfLimpo.length() != 11 || !cpfLimpo.matches("\\d{11}")) {
            throw new IllegalArgumentException("Erro na atualização: CPF inválido. Informe exatamente 11 dígitos.");
        }

        if (clienteAlterado.getTelefone() == null || clienteAlterado.getTelefone().trim().length() < 11) {
            throw new IllegalArgumentException("Erro na atualização: Telefone inválido (mínimo 11 caracteres com DDD).");
        }

        clienteDAO.atualizar(clienteAlterado);
    }

    // regras para excluir cliente
    public void removerCliente(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido.");
        }
        clienteDAO.remover(id);
    }

    public void removerCliente(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na remoção: É necessário informar um telefone válido.");
        }
        clienteDAO.remover(telefone);
    }

    public Cliente buscarClientePorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Erro na busca: O ID informado deve ser maior que zero.");
        }
        return clienteDAO.buscarPorId(id);
    }

    public List<Cliente> buscarClientesPorNome(String nomeBusca) {
        if (nomeBusca == null || nomeBusca.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na busca: Digite um nome para pesquisar.");
        }
        return clienteDAO.buscarPorNome(nomeBusca);
    }

    public List<Cliente> listarTodosClientes() {
        return clienteDAO.listarTodos();
    }
}