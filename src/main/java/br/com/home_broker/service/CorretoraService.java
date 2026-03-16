package br.com.home_broker.service;

import br.com.home_broker.application.ConnectionFactory;
import br.com.home_broker.dao.AcaoDAO;
import br.com.home_broker.dao.InvestidorDAO;
import br.com.home_broker.dao.OrdemCompraDAO;
import br.com.home_broker.model.Acao;
import br.com.home_broker.model.Investidor;
import br.com.home_broker.model.OrdemCompra;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Set;

import static java.math.BigDecimal.ZERO;

public class CorretoraService {
    private InvestidorDAO investidorDAO;
    private AcaoDAO acaoDAO;
    private OrdemCompraDAO ordemCompraDAO;
    private Connection connection;
    public CorretoraService(InvestidorDAO investidorDAO, AcaoDAO acaoDAO, OrdemCompraDAO ordemCompraDAO, Connection connection) {
        this.investidorDAO = investidorDAO;
        this.acaoDAO = acaoDAO;
        this.ordemCompraDAO = ordemCompraDAO;
        this.connection = connection;
    }

    public void cadastrarNovoInvestidor(Investidor investidor){
        investidorDAO.salvar(investidor);
    }

    public void cadastraNovaAcao(Acao acao){
        if(acao.getPreco_atual() == null || acao.getPreco_atual().compareTo(ZERO) < 0){
            throw new IllegalArgumentException("O preço da ação deve ser maior ou igual a zero.");
        }

        if(acao.getTicker() == null || acao.getTicker().trim().isEmpty()){
            throw new IllegalArgumentException("O ticker da ação não pode ser vazio.");
        }

        acaoDAO.salvar(acao);
    }

    public void atualizarPrecoAcao(String ticker, BigDecimal valor){
        if(valor.compareTo(ZERO) < 0){
            throw new IllegalArgumentException("O preço da ação deve ser maior ou igual a zero.");
        }

        acaoDAO.atualizarPreco(ticker, valor);
    }

    public Set<Acao> listarAcoes(){
        return acaoDAO.listaTodas();
    }

    public Investidor buscarInvestidor(String cpf){
        if(investidorDAO.buscarPorCPF(cpf) == null){
            throw new IllegalArgumentException("Não existe investidor com esse CPF.");
        }
        return investidorDAO.buscarPorCPF(cpf);
    }
    public Investidor buscarInvestidorPorId(Long id){
        return investidorDAO.buscarPorId(id);
    }

    public void comprarAcao(Long id, Long idAcaoCompra, Long quantidade) {
        if(quantidade <= 0){
            throw new IllegalArgumentException("A quantidade de ações deve ser maior que zero.");
        }

        try{
            connection.setAutoCommit(false);
            Investidor investidor = investidorDAO.buscarPorId(id);
            Acao acao = acaoDAO.buscaAcaoPorId(idAcaoCompra);

            if (investidor == null){
                throw new IllegalArgumentException("Investidor não encontrado");
            }

            if (acao == null){
                throw new IllegalArgumentException("Ação não encontrado");
            }
            BigDecimal precoTotal = acao.getPreco_atual().multiply(BigDecimal.valueOf(quantidade));

            if(investidor.getSaldoDisponivel().compareTo(precoTotal) < 0){
                throw new IllegalArgumentException("Saldo insuficiente. Valor da ordem: R$"+precoTotal);
            }
            investidorDAO.atualizarSaldo(id, precoTotal);
            OrdemCompra ordemCompra = new OrdemCompra(id, idAcaoCompra, quantidade, precoTotal, LocalDateTime.now());
            ordemCompraDAO.salvar(ordemCompra);

            connection.commit();

        }catch (Exception e) {
            // 7. TRATAMENTO DE ERRO CRÍTICO (Rollback)
            // Se QUALQUER coisa der errado (falta de saldo, queda na rede, erro de SQL), desfaz tudo!
            try {
                connection.rollback();
            } catch (Exception rollbackEx) {
                System.err.println("Falha catastrófica ao realizar rollback: " + rollbackEx.getMessage());
            }

            // Lança o erro para frente, para a classe Main capturar e avisar o usuário
            throw new RuntimeException("A ordem de compra foi cancelada: " + e.getMessage());

        } finally {
            // 8. Limpeza: Devolve a conexão ao seu estado normal para não afetar os próximos comandos
            try {
                connection.setAutoCommit(true);
            } catch (Exception autoCommitEx) {
                System.err.println("Erro ao restaurar o autocommit: " + autoCommitEx.getMessage());
            }
        }
    }

    public void depositarConta(Long id, BigDecimal valor){
        if(investidorDAO.buscarPorId(id) == null){
            throw new IllegalArgumentException("Investidor não encontrado.");
        }
        if(valor.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("O valor a ser depositado precisar ser maior que zero.");
        }

        investidorDAO.depositar(id, valor);
    }

    public Set<OrdemCompra> listaAcoesPorInvestidor(Long id){
        if(investidorDAO.buscarPorId(id) == null){
            throw new IllegalArgumentException("Investidor não encontrado.");
        }
        return ordemCompraDAO.acoesDoInvestidor(id);
    }
    private BigDecimal retornaPrecoAcao(Long idAcao){
        return acaoDAO.precoAcao(idAcao);
    }
}
