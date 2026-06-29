package br.edu.ufersa.poo.pizzaria.DAO;

import java.util.List;

/*Interface genérica de CRUD - utiliza interfaces" */

public interface ICrudDAO<T> {

    /*Persiste um novo objeto no banco. */
    void salvar(T obj);

    /* Busca um objeto pelo seu ID. Retorna null se não encontrado. */
    T buscarPorId(int id);

    /* Retorna todos os registros da entidade. */
    List<T> listarTodos();

    /* Atualiza um objeto já existente no banco. */
    void atualizar(T obj);

    /*Remove (ou desativa) um objeto pelo ID. */
    void remover(int id);
}