package br.edu.ufersa.poo.pizzaria.exceptions;


public class AcessoNegadoException extends RuntimeException {

    private final String operacao;
    private final String perfilAtual;

    /*Descrição da operação bloqueada ("cadastrar tipo de pizza")
      Perfil do usuário que tentou a operação ( "FUNCIONARIO")  */

    public AcessoNegadoException(String operacao, String perfilAtual) {
        super("Acesso negado: a operação \"" + operacao + "\" é restrita ao Administrador. "
                + "Perfil atual: " + perfilAtual);
        this.operacao    = operacao;
        this.perfilAtual = perfilAtual;
    }

    /* Construtor simples com mensagem direta. */
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
        this.operacao    = "";
        this.perfilAtual = "";
    }

    public String getOperacao()    { return operacao; }
    public String getPerfilAtual() { return perfilAtual; }
}