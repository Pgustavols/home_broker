import br.com.home_broker.application.ConnectionFactory;
import br.com.home_broker.dao.AcaoDAO;
import br.com.home_broker.dao.InvestidorDAO;
import br.com.home_broker.dao.OrdemCompraDAO;
import br.com.home_broker.model.Acao;
import br.com.home_broker.model.Investidor;
import br.com.home_broker.service.CorretoraService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Connection connection = new ConnectionFactory().recuperarConexao();
             Scanner scanner = new Scanner(System.in)) {

            InvestidorDAO investidorDAO = new InvestidorDAO(connection);
            AcaoDAO acaoDAO = new AcaoDAO(connection);
            OrdemCompraDAO ordemCompraDAO = new OrdemCompraDAO(connection);
            CorretoraService corretoraService = new CorretoraService(investidorDAO, acaoDAO, ordemCompraDAO, connection);

            int opcao = -1;

            // MENU PRINCIPAL (Área Pública / Admin)
            while (opcao != 0) {
                exibirMenuPrincipal();
                System.out.print("Escolha uma opção: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Por favor, digite um número válido.");
                    scanner.nextLine();
                    continue;
                }
                opcao = scanner.nextInt();
                scanner.nextLine();

                try {
                    switch (opcao) {
                        case 1:
                            System.out.println("\n--- CADASTRAR INVESTIDOR ---");
                            System.out.print("Nome: ");
                            String nome = scanner.nextLine();
                            System.out.print("CPF (somente números): ");
                            String cpf = scanner.nextLine();
                            BigDecimal saldo = BigDecimal.ZERO;

                            Investidor novoInvestidor = new Investidor(nome, cpf, saldo);
                            corretoraService.cadastrarNovoInvestidor(novoInvestidor);
                            System.out.println("✅ Investidor cadastrado com sucesso!");
                            break;

                        case 2:
                            System.out.println("\n--- CADASTRAR AÇÃO ---");
                            System.out.print("Ticker: ");
                            String ticker = scanner.nextLine().toUpperCase();
                            System.out.print("Preço Atual: ");
                            BigDecimal preco = new BigDecimal(scanner.nextLine());

                            Acao novaAcao = new Acao(ticker, preco);
                            corretoraService.cadastraNovaAcao(novaAcao);
                            System.out.println("✅ Ação cadastrada com sucesso!");
                            break;

                        case 3:
                            System.out.println("\n--- ATUALIZAR PREÇO DA AÇÃO ---");
                            System.out.print("Ticker da Ação: ");
                            String tickerAcaoAtualizar = scanner.nextLine().toUpperCase();
                            System.out.print("Novo Preço: ");
                            BigDecimal novoPreco = new BigDecimal(scanner.nextLine());

                            corretoraService.atualizarPrecoAcao(tickerAcaoAtualizar, novoPreco);
                            System.out.println("✅ Preço atualizado com sucesso!");
                            break;

                        case 4:
                            System.out.println("\n--- FAZER LOG-IN (ÁREA DO CLIENTE) ---");
                            System.out.print("Digite seu CPF: ");
                            String cpfLogin = scanner.nextLine();


                            Investidor investidorLogado = corretoraService.buscarInvestidor(cpfLogin);

                            if (investidorLogado != null) {
                                System.out.println("\n✅ Acesso autorizado! Bem-vindo(a), " + investidorLogado.getNome() + ".");

                                exibirAreaDoCliente(scanner, corretoraService, investidorLogado);
                            } else {
                                System.out.println("❌ Acesso negado. CPF não encontrado.");
                            }
                            break;

                        case 0:
                            System.out.println("Encerrando o sistema... Até logo!");
                            break;

                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                    }
                } catch (Exception e) {
                    System.err.println("❌ Erro na operação: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Erro fatal ao iniciar a aplicação: " + e.getMessage());
        }
    }

    // --- NOVO MÉTODO: O SUB-MENU DO CLIENTE ---
    private static void exibirAreaDoCliente(Scanner scanner, CorretoraService corretoraService, Investidor investidorLogado) {
        int opcaoCliente = -1;

        while (opcaoCliente != 0) {
            System.out.println("\n===================================================");
            System.out.println("             HOME BROKER - CONTA: " + investidorLogado.getNome());
            System.out.println("===================================================");
            System.out.println("1. Ver Painel de Ações (Cotação Atual)");
            System.out.println("2. Consultar Meu Saldo");
            System.out.println("3. Comprar Ação (Executar Ordem)");
            System.out.println("4. Fazer depósito");
            System.out.println("5. Listar Ações");
            System.out.println("0. Fazer Logout (Sair da Conta)");
            System.out.println("===================================================");
            System.out.print("Escolha uma opção: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Por favor, digite um número válido.");
                scanner.nextLine();
                continue;
            }
            opcaoCliente = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (opcaoCliente) {
                    case 1:
                        System.out.println("\n--- PAINEL DE AÇÕES ---");
                        corretoraService.listarAcoes().forEach(System.out::println);
                        break;

                    case 2:
                        System.out.println("\n--- MEU SALDO ---");
                        Investidor saldoAtualizado = corretoraService.buscarInvestidorPorId(investidorLogado.getId());
                        System.out.println("Saldo Disponível: R$ " + saldoAtualizado.getSaldoDisponivel());
                        break;

                    case 3:
                        System.out.println("\n--- EXECUTAR ORDEM DE COMPRA ---");
                        System.out.print("ID da Ação que deseja comprar: ");
                        Long idAcaoCompra = scanner.nextLong();
                        scanner.nextLine();
                        System.out.print("Quantidade: ");
                        Long quantidade = scanner.nextLong();
                        scanner.nextLine();

                        // Pegamos o ID do investidor automaticamente do objeto logado!
                        corretoraService.comprarAcao(investidorLogado.getId(), idAcaoCompra, quantidade);
                        System.out.println("✅ Ordem de compra executada com sucesso!");
                        break;

                    case 4:
                        System.out.println("\n--- REALIZAR DEPÓSITO ---");
                        System.out.print("Informe o valor do depósito: ");
                        BigDecimal valorDeposito = new BigDecimal(scanner.nextLine());
                        corretoraService.depositarConta(investidorLogado.getId(), valorDeposito);
                        System.out.println("✅ Depósito Realizado com sucesso!");
                        break;

                    case 5:
                        System.out.println("\n--- LISTA DE AÇÕES ---");
                        corretoraService.listaAcoesPorInvestidor(investidorLogado.getId()).stream().forEach(System.out::println);
                        break;
                    case 0:
                        System.out.println("Fazendo logout...");
                        break;

                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro na operação: " + e.getMessage());
            }
        }
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n===================================================");
        System.out.println("             SISTEMA DA CORRETORA");
        System.out.println("===================================================");
        System.out.println("1. Cadastrar Novo Investidor");
        System.out.println("2. Cadastrar Nova Ação na Bolsa");
        System.out.println("3. Atualizar Preço de uma Ação");
        System.out.println("4. Entrar na Conta (Login Cliente)");
        System.out.println("0. Sair do Sistema");
        System.out.println("===================================================");
    }
}