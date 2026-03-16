package br.com.home_broker.dao;

import br.com.home_broker.model.Acao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class AcaoDAO {
    protected Connection conn;

    public AcaoDAO(Connection connection){
        this.conn = connection;
    }

    public void salvar(Acao acao){
        String sql = "INSERT INTO acao (ticker, preco_atual) values (?, ?)";

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, acao.getTicker());
            ps.setBigDecimal(2, acao.getPreco_atual());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Acao> listaTodas(){
        String sql = "SELECT * FROM acao ORDER BY id ASC";
        Set<Acao> acoes = new HashSet<>();

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Long id = rs.getLong(1);
                    String ticker = rs.getString(2);
                    BigDecimal preco_atual = rs.getBigDecimal(3);
                    Acao acao = new Acao(id, ticker, preco_atual);

                    acoes.add(acao);
                }
            }
            return acoes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public Acao buscaPorTicker(String ticker){
//        String sql = "SELECT * FROM acao where ticker = ?";
//        Acao acao;
//        try(PreparedStatement ps = conn.prepareStatement(sql)){
//            ps.setString(1, ticker);
//            ResultSet rs = ps.executeQuery();
//            if(rs.next()){
//                Long id = rs.getLong(1);
//                String numeroAcao = rs.getString(2);
//                BigDecimal preco_atual = rs.getBigDecimal(3);
//                acao = new Acao(id, numeroAcao, preco_atual);
//                return acao;
//            }
//            return null;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

//    }

    public Acao buscaAcaoPorId(Long id){
        String sql = "SELECT * FROM acao where id = ?";
        Acao acao;
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Long idAcao = rs.getLong(1);
                String numeroAcao = rs.getString(2);
                BigDecimal preco = rs.getBigDecimal(3);
                acao = new Acao(idAcao, numeroAcao, preco);
                return acao;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public int atualizarPreco(String ticker, BigDecimal novoPreco){
        String sql = "UPDATE acao SET preco_atual = ? WHERE ticker = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setBigDecimal(1, novoPreco);
            ps.setString(2, ticker);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public BigDecimal precoAcao(Long idAcao){
        String sql = "SELECT preco_atual FROM acao WHERE id = ?";

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, idAcao);
            try(ResultSet rs = ps.executeQuery()){
                return rs.getBigDecimal(1);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
