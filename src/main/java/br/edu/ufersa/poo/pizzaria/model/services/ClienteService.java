package br.edu.ufersa.poo.pizzaria.model.services;

import br.edu.ufersa.poo.pizzaria.DAO.ClienteDAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import java.util.List;

public class ClienteService {

    // Service necessita da dao
    private final ClienteDAO clienteDAO = new ClienteDAO();

    // regras de cadastro do cliente
    public void cadastrarCliente(String nome, String endereco, String cpf, String telefone, String bairro) {
        // 1. Validações de Regra de Negócio (Campos Obrigatórios)
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro no cadastro: Precisa ter nome!");
        }
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro no cadastro: Precisa ter endereço.");
        }
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro no cadastro: Precisa ter bairro.");
        }

        // cpf e telefone tem pelo menos 11 digitos, e por esses 2 serem strings, '-', '.', '()' podem aparecer
        if (cpf == null || cpf.trim().length() < 11) {
            throw new IllegalArgumentException("Erro no cadastro: CPF inválido ou incompleto (Mínimo 11 caracteres).");
        }
        if (telefone == null || telefone.trim().length() < 11) {
            throw new IllegalArgumentException("Erro no cadastro: Telefone inválido (Mínimo 11 caracteres com DDD).");
        }

        // após a aplicação dos casos, instancia o cliente novo
        Cliente novoCliente = new Cliente(nome, endereco, cpf, telefone, bairro);

        // 4. Envia para a DAO salvar de fato, com a auto inserção do ID
        clienteDAO.salvar(novoCliente);
    }

    // regras para atualizar dados de um cliente
    public void atualizarCliente(Cliente clienteAlterado) {
        // checa se o objeto ou o ID são válidos
        if (clienteAlterado == null || clienteAlterado.getIdCliente() <= 0) {
            throw new IllegalArgumentException("Erro para atualização: Cliente inválido ou ID não informado.");
        }

        // 2.checa se o cliente  existe no bd antes de tentar mudar algum dado
        Cliente clienteExistente = clienteDAO.buscarPorId(clienteAlterado.getIdCliente());
        if (clienteExistente == null) {
            throw new IllegalArgumentException("Erro na atualização: Cliente não encontrado no sistema.");
        }

        // checado se é permitido alterar, agora as regras de update de dados
        if (clienteAlterado.getNome() == null || clienteAlterado.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na atualização: O nome não pode ficar vazio.");
        }
        if (clienteAlterado.getEndereco() == null || clienteAlterado.getEndereco().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na atualização: O endereço não pode ficar vazio.");
        } // garantir que nao tenha informações vazias

        // 4. caso validado, manda para a DAO executar o UPDATE no banco
        clienteDAO.atualizar(clienteAlterado);
    }

    //regras para excluir cliente
    public void removerCliente(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na remoção: É necessário informar um telefone válido.");
        } // para remover o cliente deve-se informar o seu telefone nesse metodo, o qual n pode ser vazio

        clienteDAO.remover(telefone); //dao irá entao apagar do bd
    }

    // regras para buscar o cliente via id
    public Cliente buscarClientePorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Erro na busca: O ID informado deve ser maior que zero.");
        }
        return clienteDAO.buscarPorId(id); // id inicia em 0 por lógica, caso o valor do id seja valido a dao irá filtrar a busca
    }

    //regra para buscar pelo nome
    public List<Cliente> buscarClientesPorNome(String nomeBusca) {
        if (nomeBusca == null || nomeBusca.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na busca: Digite um nome para pesquisar.");
        } // nome n pode ser vazio
        return clienteDAO.buscarPorNome(nomeBusca); //dao vai checar a string e filtrar a busca
    }

    public List<Cliente> listarTodosClientes() {
        return clienteDAO.listarTodos();
    }
}