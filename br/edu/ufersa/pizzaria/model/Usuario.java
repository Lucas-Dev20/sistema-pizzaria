package br.edu.ufersa.pizzaria.model;

public class Usuario 
{
   
    private String login;
    private String senha;
    private String cargo;

    public Usuario(String login, String senha, String cargo) //construtor
    {
        setLogin(login);
        setSenha(senha);
        setCargo(cargo);
    }
//gets e sets para restrições 

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login) 
    {
        if (login != null && login.trim().length() >= 3) // login deve ter minimo 3 caracteres
        {
            this.login = login;
        } 
        else 
        {
            throw new IllegalArgumentException("O login deve ter no mínimo 3 caracteres."); // aviso de erro 
        }
    }

    public String getSenha() 
    {
        return senha;
    }

    public void setSenha(String senha)
    {
        if (senha != null && senha.length() >= 6) // senha deve ter minimo 6 caracteres
        {
            this.senha = senha;
        } 
        else
        {
            throw new IllegalArgumentException("A senha deve ter no mínimo 6 caracteres."); // aviso de erro 
        }
    }

    public String getCargo() 
    {
        return cargo;
    }

    public void setCargo(String cargo) 
    {
        if (cargo != null && !cargo.trim().isEmpty()) // o cargo não pode ser string vazia
        {
            this.cargo = cargo;
        }
        else 
        {
            throw new IllegalArgumentException("O cargo não pode estar vazio.");
        }
    }
    
    public boolean autenticar(String loginInformado, String senhaInformada) 
    {
        if (this.login.equals(loginInformado) && this.senha.equals(senhaInformada)) //compara as strings para ver se o login tá correto
        {
            System.out.println("Login bem sucedido ");
            return true;
        }
        else 
        {
        System.out.println("ERRO -> Login ou senha incorretos.");
        return false;
        }
    }

    public void esqueciASenha(String loginInformado) 
    {
        if (this.login.equals(loginInformado))
        {
            System.out.println("Um link de recuperação será enviado para o email do usuário "); // login do usuario existe, então é enviado para ele a recuperação
        } 
        else 
        {
            System.out.println("Erro -> Usuário não existe");
        }
    }

}