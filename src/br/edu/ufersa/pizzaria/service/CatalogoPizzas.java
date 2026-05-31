package br.edu.ufersa.pizzaria.model;

public class CatalogoPizzas
 {
    private Pizza[] pizzas;
    private Adicional[] adicionais;
    
    private int qtdPizzas = 0;
    private int qtdAdicionais = 0; // ao cadastrar, haverá incremento dessa variável para controlar a quantidade de itens cadastrados

    public CatalogoPizzas() 
    { // construtor para inicializar os arrays com uma capacidade pré-definida
        this.pizzas = new Pizza[1000]; 
        this.adicionais = new Adicional[1000]; 
    }

    public void cadastrarPizza(Pizza p, Usuario u)
    {
        if (u != null && u.getCargo().trim().equalsIgnoreCase("admin")) //somente admin pode cadastrar entao verifica se o cargo logado é o admin
        {
            if (qtdPizzas < pizzas.length) // o array deve ter espaço para cadastrar a nova pizza, ou seja, a quantidade de pizzas cadastradas deve ser menor que o tamanho do array, para evitar estouro de array
            {
              this.pizzas[qtdPizzas] = p; // a nova pizza é adicionada na posição indicada por qtdPizzas, que é a próxima posição disponível no array, e depois é incrementada para apontar para a próxima posição disponível
              qtdPizzas++;
              System.out.println("SUCESSO: Pizza sabor " + p.getTipo() + " cadastrada. ");
            } 
            else 
            {
                System.out.println("erro ao cadastrar, limite de pizzas atingido.");
            }
        
        }
        else 
        {
        // caso o cargo não for administrador, a operação é bloqueada
        System.out.println("ACESSO NEGADO: Apenas admin pode cadastrar novas pizzas.");
        }
    }

    public void editarPizza(Pizza p) // metodo para editar uma pizza, recebe a pizza com as informações atualizadas, e procura no array pela pizza com o mesmo nome para substituir as informações, caso encontre, é editada, caso contrário, não é feita nenhuma alteração
    {
        for (int i = 0; i < qtdPizzas; i++) //loop para encontrar a pizza a ser editada, comparando pelo nome
        {
            if (pizzas[i].getTipo().equalsIgnoreCase(p.getTipo()))  // compara o nome da pizza no array com o nome da pizza passada no parametro, e é considerado igual maisculo ou minusculo
            {
                pizzas[i] = p; // substitui a pizza no array pela pizza atualizada passada no parametro
                System.out.println("Sabor editado com sucesso.");
                return; // sai do método após sucesso
            }
            else
            {
                System.out.println("Sabor não encontrado para ser atualizado."); // caso a pizza não seja encontrada, é exibida uma mensagem de aviso
            }
        } // se nao achar a devida pizza o metodo encerra
    }

    public void excluirPizza(Pizza p) 
    {
        for (int i = 0; i < qtdPizzas; i++) // percorre até a quantidade de pizzas cadastradas para encontrar a pizza a ser excluída, comparando pelo nome
        {
            if (pizzas[i].equals(p)) //compara a string e acha a pizza a ser excluida
            {
                pizzas[i] = pizzas[qtdPizzas - 1];  //a pizza a ser excluida terá em sua posição a pizza que está na última posição do array, para evitar lacuna
                pizzas[qtdPizzas - 1] = null; // a última posição do array, que agora tem a pizza que foi movida para a posição da pizza excluída, é setada como null para evitar que haja uma referência a uma pizza que não existe mais
                qtdPizzas--; // decrementa a quantidade de pizzas cadastradas, ja q uma pizza foi excluída
                System.out.println("Sabor excluído com sucesso.");
                return; // sai do método após exclusão
            }
        }
    }

    public Pizza[] buscarPizzaPorTipo(String saborInformado) 
    {
        int cont = 0;
        for (int i = 0; i < qtdPizzas; i++) // percorre o array de pizzas até a quantidade de pizzas cadastradas para contar quantas pizzas correspondem ao sabor informado, comparando pelo nome
        {
            if (pizzas[i].getTipo().equalsIgnoreCase(saborInformado)) 
            {
                cont++; // incrementa o contador para cada pizza que corresponde ao sabor informado, considerando iguais as maiúsculas e minúsculas
            }
        }

        Pizza[] resultados = new Pizza[cont]; // cria um array de pizzas com o tamanho do contador, para armazenar as pizzas que correspondem ao sabor informado
        int aux = 0; // variável auxiliar para controlar a posição no array de resultados

        for (int i = 0; i < qtdPizzas; i++) // loop para preencher o array de resultados com as pizzas que correspondem ao sabor informado, comparando pelo nome
        {
            if (pizzas[i].getTipo().equalsIgnoreCase(saborInformado)) 
            {
                resultados[aux] = pizzas[i]; // adiciona a pizza que corresponde ao sabor informado na posição indicada por aux no array de resultados
                aux++;
            }
        }
        return resultados; // retorna o array de pizzas que correspondem ao sabor informado, ou um array vazio caso nenhuma pizza corresponda
    }
    
    public Adicional[] buscarAdicionalPorNome(String nome) // mesma ideia do metodo anterior para buscar pizza por sabor
    {
        int cont = 0;
        for (int i = 0; i < qtdAdicionais; i++)
        {
            if (adicionais[i].getNome().equalsIgnoreCase(nome)) 
            {
                cont++;
            }
        }

        Adicional[] resultados = new Adicional[cont];
        int aux = 0;

        for (int i = 0; i < qtdAdicionais; i++) 
        {
            if (adicionais[i].getNome().equalsIgnoreCase(nome)) 
            {
                resultados[aux] = adicionais[i];
                aux++;
            }
        }
        return resultados; // resultados é exatamente o array de adicionais que correspondem ao nome informado, ou um array vazio caso não corresponda
    }
}