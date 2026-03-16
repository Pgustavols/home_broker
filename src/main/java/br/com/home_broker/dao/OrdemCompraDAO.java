package br.com.home_broker.dao;

import br.com.home_broker.model.OrdemCompra;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


public class OrdemCompraDAO {
    protected Connection conn;
    public OrdemCompraDAO(Connection connection){
        this.conn = connection;
    }

    public void salvar(OrdemCompra ordemCompra){
        String sql = "INSERT INTO ordem_compra(id_investidor, id_acao, quantidade, preco_total, data_compra) values(?, ?, ?, ?, ?)";

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, ordemCompra.getIdInvestidor());
            ps.setLong(2, ordemCompra.getIdAcao());
            ps.setLong(3, ordemCompra.getQuantidade());
            ps.setBigDecimal(4, ordemCompra.getPrecoTotal());
            ps.setObject(5, LocalDateTime.now());
            ps.execute();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Set<OrdemCompra> acoesDoInvestidor(Long id){
        Set<OrdemCompra> compras = new HashSet<>();
        String sql = "SELECT oc.id_investidor, a.ticker, SUM(oc.quantidade) as quantidade, SUM(oc.preco_total) as preco_total \n" +
                "FROM ordem_compra oc INNER JOIN acao a ON oc.id_acao = a.id GROUP BY id_investidor, a.ticker\n" +
                "having id_investidor = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    String ticker = rs.getString(2);
                    Long quantidade = rs.getLong(3);
                    BigDecimal precoTotal = rs.getBigDecimal(4);
                    OrdemCompra ordemCompra = new OrdemCompra(ticker, quantidade, precoTotal);
                    compras.add(ordemCompra);
                }
                return compras;
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
