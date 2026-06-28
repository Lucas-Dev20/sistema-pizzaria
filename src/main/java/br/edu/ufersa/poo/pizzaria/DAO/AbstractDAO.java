package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/* PADRÃO TEMPLATE METHOD */
public abstract class AbstractDAO<T> implements ICrudDAO<T> {

    // ── Hooks abstratos — cada DAO concreto define o seu ─────────────────────

    /** Retorna o SQL de INSERT desta entidade. */
    protected abstract String getInsertSQL();

    /** Preenche os parâmetros do PreparedStatement para o INSERT. */
    protected abstract void preencherInsert(PreparedStatement ps, T obj) throws SQLException;

    /** Retorna o nome da tabela no banco de dados. */
    protected abstract String getTabela();

    /** Converte uma linha do ResultSet no objeto de domínio. */
    protected abstract T mapear(ResultSet rs) throws SQLException;

    // ── Template: salvar() ────────────────────────────────────────────────────
    @Override
    public void salvar(T obj) {
        String sql = getInsertSQL();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            preencherInsert(ps, obj);
            ps.executeUpdate();
            System.out.println("[" + getTabela() + "] Registro salvo com sucesso.");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar em " + getTabela() + ": " + e.getMessage(), e);
        }
    }

    // ── Template: listarTodos() ───────────────────────────────────────────────
    @Override
    public List<T> listarTodos() {
        List<T> lista = new ArrayList<>();
        String sql = "SELECT * FROM " + getTabela();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar " + getTabela() + ": " + e.getMessage(), e);
        }
        return lista;
    }

    // buscarPorId(), atualizar() e remover() são específicos demais
    // para virar template genérico — cada DAO os implementa diretamente.
}