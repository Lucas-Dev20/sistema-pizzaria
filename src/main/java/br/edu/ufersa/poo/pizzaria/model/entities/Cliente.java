package br.edu.ufersa.poo.pizzaria.model.entities;

// Cliente herda de Pessoa (requisito de herança e polimorfismo)

public class Cliente extends Pessoa
{
    private int idCliente = -1; //novo cliente ao cadastrar
    private String endereco;
    private String cpf;
    private String telefone;
    private String bairro;

    // CONSTRUTOR NOVO que inclui o id
    public Cliente(int id, String nome, String endereco, String cpf, String telefone, String bairro) {
        super(nome);           // passa nome para a classe abstrata Pessoa
        this.idCliente = id;
        this.endereco = endereco;
        this.cpf = cpf;
        this.telefone = telefone;
        this.bairro = bairro;
    }

    public Cliente(String nome, String endereco, String cpf, String telefone, String bairro)
    {
        super(nome);           // passa nome para a classe abstrata Pessoa
        setEndereco(endereco);
        setCpf(cpf);
        setTelefone(telefone);
        setBairro(bairro);
    }

    // ── Polimorfismo: implementa método abstrato de Pessoa ────────────────────
    @Override
    public String getDescricao() {
        return "Cliente: " + getNome() + " | CPF: " + cpf + " | Tel: " + telefone;
    }

    // gets e sets para restrições

    public int getIdCliente() { return idCliente; }

    public void setIdCliente(int idCliente) {
        if (idCliente > 0) {
            this.idCliente = idCliente;}
    }

    // getNome() e setNome() herdados de Pessoa
    // Sobrescreve setNome() para manter validação
    @Override
    public void setNome(String nome)
    {
        if (nome != null && !nome.trim().isEmpty())
        {
            super.setNome(nome);
        }
        else
        {
            throw new IllegalArgumentException("O nome não pode estar vazio.");
        }
    }

    public String getEndereco()
    {
        return endereco;
    }

    public void setEndereco(String endereco)
    {
        if (endereco != null && !endereco.trim().isEmpty()) //caso não esteja vazio, tendo desconsiderado espaços nos extremos da string, valida o endereço
        {
            this.endereco = endereco;
        }
        else
        {
            throw new IllegalArgumentException("O endereço não pode estar vazio.");
        }
    }

    public String getCpf()
    {
        return cpf;
    }

    public void setCpf(String cpf)
    {
        if (cpf != null && cpf.trim().length() >= 11) //cpf tem 11 digitos, está minimo 11 pois pode ter pontos e traços casualmente
        {                                                   //ex.: 111.222.333-40
            this.cpf = cpf;
        }
        else
        {
            throw new IllegalArgumentException("O CPF deve ter no mínimo 11 caracteres.");
        }
    }

    public String getTelefone()
    {
        return telefone;
    }

    public void setTelefone(String telefone)
    {
        if (telefone != null && telefone.trim().length() >= 11) //numero de telefone tem 11 digitos, está minimo 11 pois pode ter () no ddd e traços casualmente
        {
            this.telefone = telefone;
        }
        else
        {
            throw new IllegalArgumentException("O telefone deve ter no mínimo 11 caracteres.");
        }
    }

    public String getBairro()
    {
        return bairro;
    }

    public void setBairro(String bairro)
    {
        if (bairro != null && !bairro.trim().isEmpty()) //caso não esteja vazio, tendo desconsiderado espaços nos extremos da string, valida o bairro
        {
            this.bairro = bairro;
        }
        else
        {
            throw new IllegalArgumentException("O bairro não pode estar vazio.");
        }
    }
}