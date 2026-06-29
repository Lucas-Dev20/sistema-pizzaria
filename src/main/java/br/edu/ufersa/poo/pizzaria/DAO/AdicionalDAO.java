package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PADRÃO TEMPLATE METHOD — AdicionalDAO extends AbstractDAO<Adicional>
 *
 * Os métodos salvar() e listarTodos() são herdados de AbstractDAO.
 * Aqui só precisamos implementar os 4 hooks abstratos (getInsertSQL,
 * preencherInsert, getTabela, mapear) e manter os métodos específicos
 * desta entidade (baixarEstoque, reporEstoque, buscarPorNome, remover por nome).
 */
public class AdicionalDAO extends AbstractDAO<Adicional> {

    // ══════════════════════════════════════════════════════════════════════════
    //  HOOKS DO TEMPLATE METHOD — implementação obrigatória da AbstractDAO
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO adicional (nome, valor, quantidade) VALUES (?, ?, ?)";
    }

    @Override
    protected void preencherInsert(PreparedStatement ps, Adicional adicional) throws SQLException {
        ps.setString(1, adicional.getNome());
        ps.setDouble(2, adicional.getValor());
        ps.setInt(3, adicional.getQtd());
    }

    @Override
    protected String getTabela() {
        return "adicional";
    }

    @Override
    protected Adicional mapear(ResultSet rs) throws SQLException {
        return new Adicional(
                rs.getInt("id_adicional"),
                rs.getString("nome"),
                rs.getDouble("valor"),
                rs.getInt("quantidade")
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  OVERRIDE: salvar — versão que retorna o ID gerado (mantém compatibilidade)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Sobrescreve o salvar() do AbstractDAO para retornar o ID gerado.
     * Necessário porque AdicionalService usa o ID logo após o INSERT.
     */
    @Override
    public void salvar(Adicional adicional) {
        salvarERetornarId(adicional);
    }

    /** Versão com retorno de ID — usada por AdicionalService.cadastrarAdicional(). */
    public int salvarERetornarId(Adicional adicional) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getInsertSQL(), Statement.RETURN_GENERATED_KEYS)) {

            preencherInsert(stmt, adicional);
            stmt.executeUpdate();
            System.out.println("[adicional] Registro salvo com sucesso.");

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Erro no INSERT de Adicional:");
            e.printStackTrace();
        }
        return -1;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CRUD ESPECÍFICO — métodos não cobertos pelo template genérico
    // ══════════════════════════════════════════════════════════════════════════

    /** buscarPorId — implementação obrigatória de ICrudDAO (via AbstractDAO). */
    @Override
    public Adicional buscarPorId(int idBusca) {
        String sql = "SELECT * FROM adicional WHERE id_adicional = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBusca);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar adicional por ID:");
            e.printStackTrace();
        }
        return null;
    }

    /** Busca pelo nome — usado após INSERT para obter o ID gerado. */
    public Adicional buscarPorNome(String nome) {
        String sql = "SELECT * FROM adicional WHERE nome = ? ORDER BY id_adicional DESC LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar adicional por nome:");
            e.printStackTrace();
        }
        return null;
    }

    /** atualizar — implementação obrigatória de ICrudDAO (via AbstractDAO). */
    @Override
    public void atualizar(Adicional adicional) {
        atualizar(adicional, adicional.getNome());
    }

    /**
     * Versão com nomeAntigo — necessária porque Adicional não tem chave
     * numérica obrigatória na busca de atualização (pode-se mudar o próprio nome).
     */
    public void atualizar(Adicional adicional, String nomeAntigo) {
        String sql = "UPDATE adicional SET nome = ?, valor = ?, quantidade = ? WHERE nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, adicional.getNome());
            stmt.setDouble(2, adicional.getValor());
            stmt.setInt(3, adicional.getQtd());
            stmt.setString(4, nomeAntigo);

            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                System.out.println("Adicional atualizado com sucesso!");
            } else {
                System.out.println("Nenhum adicional encontrado com o nome informado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro no UPDATE de Adicional:");
            e.printStackTrace();
        }
    }

    /** remover por ID — implementação obrigatória de ICrudDAO (via AbstractDAO). */
    @Override
    public void remover(int id) {
        Adicional a = buscarPorId(id);
        if (a != null) remover(a.getNome());
    }

    /**
     * remover por nome — faz DELETE em cascata via transação:
     * pedido_adicional → reposicao_estoque → adicional
     */
    public void remover(String nomeAdicional) {
        String sqlBuscaId        = "SELECT id_adicional FROM adicional WHERE nome = ?";
        String sqlPedidoAdicional = "DELETE FROM pedido_adicional WHERE id_adicional = ?";
        String sqlReposicao      = "DELETE FROM reposicao_estoque WHERE id_adicional = ?";
        String sqlAdicional      = "DELETE FROM adicional WHERE id_adicional = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Busca o ID pelo nome
                int idAdicional = -1;
                try (PreparedStatement stmt = conn.prepareStatement(sqlBuscaId)) {
                    stmt.setString(1, nomeAdicional);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) idAdicional = rs.getInt("id_adicional");
                    }
                }

                if (idAdicional == -1) {
                    System.out.println("Nenhum adicional encontrado com o nome: " + nomeAdicional);
                    conn.rollback();
                    return;
                }

                // 2. Remove referências em pedido_adicional
                try (PreparedStatement stmt = conn.prepareStatement(sqlPedidoAdicional)) {
                    stmt.setInt(1, idAdicional);
                    stmt.executeUpdate();
                }

                // 3. Remove histórico de reposição
                try (PreparedStatement stmt = conn.prepareStatement(sqlReposicao)) {
                    stmt.setInt(1, idAdicional);
                    stmt.executeUpdate();
                }

                // 4. Remove o adicional
                try (PreparedStatement stmt = conn.prepareStatement(sqlAdicional)) {
                    stmt.setInt(1, idAdicional);
                    int linhas = stmt.executeUpdate();
                    if (linhas > 0) System.out.println("Adicional '" + nomeAdicional + "' removido com sucesso!");
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Erro no DELETE de Adicional — rollback executado:");
                e.printStackTrace();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao conectar para remover adicional:");
            e.printStackTrace();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MÉTODOS DE ESTOQUE — específicos de Adicional
    // ══════════════════════════════════════════════════════════════════════════

    /** Desconta quantidade do estoque do adicional no banco. */
    public void baixarEstoque(int idAdicional, int quantidade) {
        String sql = "UPDATE adicional SET quantidade = quantidade - ? WHERE id_adicional = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setInt(2, idAdicional);

            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                System.out.println("Estoque do adicional (ID: " + idAdicional + ") reduzido em " + quantidade);
            } else {
                System.out.println("Nenhum adicional encontrado com o ID: " + idAdicional);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao baixar estoque do adicional:");
            e.printStackTrace();
        }
    }

    /** Repõe quantidade no estoque do adicional no banco. */
    public void reporEstoque(int idAdicional, int quantidade) {
        String sql = "UPDATE adicional SET quantidade = quantidade + ? WHERE id_adicional = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setInt(2, idAdicional);

            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                System.out.println("Estoque do adicional (ID: " + idAdicional + ") reposto em " + quantidade);
            } else {
                System.out.println("Nenhum adicional encontrado com o ID: " + idAdicional);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao repor estoque do adicional:");
            e.printStackTrace();
        }
    }
}