package br.edu.ufersa.pizzaria.model;

public class Cliente 
{
    private String nome;
    private String endereco;
    private String cpf;
    private String telefone;
    private String bairro;

    public Cliente(String nome, String endereco, String cpf, String telefone, String bairro)
    {
        setNome(nome);
        setEndereco(endereco);
        setCpf(cpf);
        setTelefone(telefone);
        setBairro(bairro);
    }

    // gets e sets para restrições

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome)
    {
        if (nome != null && !nome.trim().isEmpty()) //caso não esteja vazio, tendo desconsiderado espaços nos extremos da string, valida o nome
        {
            this.nome = nome;
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