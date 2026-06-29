package br.edu.ufersa.poo.pizzaria.model.services;

import br.edu.ufersa.poo.pizzaria.DAO.AdicionalDAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.DAO.ReposicaoEstoqueDAO;
import java.util.List;

public class AdicionalService {

    //service precisa da dao
    private final AdicionalDAO adicionalDAO = new AdicionalDAO();
    private final ReposicaoEstoqueDAO reposicaoDAO = new ReposicaoEstoqueDAO();

    public int cadastrarAdicional(String nome, double valor, int quantidade) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro no cadastro: O nome do adicional é obrigatório.");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("Erro no cadastro: O valor do adicional deve ser maior que zero.");
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException("Erro no cadastro: A quantidade inicial em estoque não pode ser negativa.");
        }
        Adicional novoAdicional = new Adicional(nome, valor, quantidade);
        return adicionalDAO.salvarERetornarId(novoAdicional);
    }

    /*Cadastra um novo adicional E registra a entrada inicial na reposicao_estoque.
     Usa o id retornado pelo INSERT — sem depender do nome para buscar. */

    public void cadastrarAdicionalComReposicao(String nome, double custoUnitario, int quantidade) {
        int idGerado = cadastrarAdicional(nome, custoUnitario, quantidade);
        if (idGerado > 0) {
            reposicaoDAO.registrarReposicao(idGerado, quantidade, custoUnitario);
        }
    }

    /*Repõe o estoque usando o custo informado pelo usuário*/

    public void creditarEstoqueComCusto(int idAdicional, int quantidade, double custoUnitario) {
        if (idAdicional <= 0) throw new IllegalArgumentException("ID do adicional inválido.");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        if (custoUnitario < 0) throw new IllegalArgumentException("Custo não pode ser negativo.");

        Adicional adicional = adicionalDAO.buscarPorId(idAdicional);
        if (adicional == null) throw new IllegalArgumentException("Adicional não encontrado.");

        // 1. Sobe a quantidade no banco
        adicionalDAO.reporEstoque(idAdicional, quantidade);

        // 2. Registra uma reposição com o custo correto
        reposicaoDAO.registrarReposicao(idAdicional, quantidade, custoUnitario);
    }

    //regra para mudança na quantidade de estoque
    public void consumirEstoque(int idAdicional, int quantidadeConsumida) {
        if (idAdicional <= 0) {
            throw new IllegalArgumentException("Erro no estoque: ID do adicional inválido.");
        }
        if (quantidadeConsumida <= 0) {
            throw new IllegalArgumentException("Erro no estoque: A quantidade a ser baixada deve ser maior que zero.");
        }

        //busca o adicional pelo id para update
        Adicional adicional = adicionalDAO.buscarPorId(idAdicional);
        if (adicional == null) {
            throw new IllegalArgumentException("Erro no estoque: Adicional não encontrado no sistema.");
        }

        //sem estoque suficiente n é possivel update
        if (adicional.getQtd() < quantidadeConsumida) {
            throw new IllegalArgumentException("Erro no estoque: quantidade insuficiente de adicional '"
                    + adicional.getNome() + "'. Estoque atual: " + adicional.getQtd());
        }

        //caso passe pelas etapas anteriores a dao vai entao baixar a quantidade do devido adicional
        adicionalDAO.baixarEstoque(idAdicional, quantidadeConsumida);
    }

    //regra para repor estoque -> checagem basica de id e quantidade validas logicamente
    public void creditarEstoque(int idAdicional, int quantidadeReposta) {

        if (idAdicional <= 0) {
            throw new IllegalArgumentException(
                    "Erro no estoque: ID do adicional inválido."
            );
        }

        if (quantidadeReposta <= 0) {
            throw new IllegalArgumentException(
                    "Erro no estoque: A quantidade de reposição deve ser maior que zero."
            );
        }

        Adicional adicional =
                adicionalDAO.buscarPorId(idAdicional);

        if (adicional == null) {
            throw new IllegalArgumentException(
                    "Erro no estoque: Adicional não encontrado."
            );
        }

        adicionalDAO.reporEstoque(
                idAdicional,
                quantidadeReposta
        );


        reposicaoDAO.registrarReposicao(
                idAdicional,
                quantidadeReposta,
                adicional.getValor()
        );
    }

    //regra para atualizar dados de um adicional já existente
    public void atualizarAdicional(Adicional adicionalAlterado, String nomeAntigo) {
        if (adicionalAlterado == null) {
            throw new IllegalArgumentException("Erro na atualização: Sem informação do adicional alterado.");
        }
        if (nomeAntigo == null || nomeAntigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na atualização: É necessário informar o nome atual do adicional.");
        }
        if (adicionalAlterado.getValor() <= 0) {
            throw new IllegalArgumentException("Erro na atualização: O valor deve ser maior que zero.");
        } //checa validade dos dados para update, como novo nome e valor, além do então nome que será alterado

        //dao vai atualizar o adicional
        adicionalDAO.atualizar(adicionalAlterado, nomeAntigo);
    }

    //regra para remover adicional do bd
    public void removerAdicional(String nomeAdicional) {
        if (nomeAdicional == null || nomeAdicional.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na remoção: Informe o nome do adicional a ser excluído.");
        } //checa se a entrada é valida com nome do adicional
        //dao vai então remover do bd
        adicionalDAO.remover(nomeAdicional);
    }

    //regra para busca de adicional pelo id
    public Adicional buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Erro na busca: O ID informado deve ser maior que zero.");
        }
        return adicionalDAO.buscarPorId(id); //checa se o id é inteiro positivo, se sim vai retornar o devido adicional
    }

    public List<Adicional> listarTodosAdicionais() {
        return adicionalDAO.listarTodos(); // select * padrão geral, vai exibie todos os adicionais
    }
}