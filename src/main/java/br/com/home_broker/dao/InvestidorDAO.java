package br.com.home_broker.dao;

import br.com.home_broker.model.Investidor;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InvestidorDAO {
    protected Connection conn;
    public InvestidorDAO(Connection connection){
        this.conn = connection;
    }

    public void salvar(@NotNull Investidor investidor){
        String sql = "INSERT INTO investidor(nome, cpf, saldo_disponivel) values(?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, investidor.getNome());
            ps.setString(2, investidor.getCpf());
            ps.setBigDecimal(3, investidor.getSaldoDisponivel());
            ps.execute();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Investidor buscarPorId(Long id){
        Investidor investidor;
        String sql = "SELECT nome, cpf, saldo_disponivel FROM investidor WHERE id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    investidor = new Investidor(rs.getString(1), rs.getString(2), rs.getBigDecimal(3));
                    return investidor;
                }
            }
            return null;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Investidor buscarPorCPF(String cpf){
        Investidor investidor;
        String sql = "SELECT * FROM investidor WHERE cpf = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, cpf);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    investidor = new Investidor(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getBigDecimal(4));
                    return investidor;
                }
            }
            return null;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void atualizarSaldo(Long id, BigDecimal valor){
        String sql = "UPDATE investidor SET saldo_disponivel = saldo_disponivel - ? WHERE id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setBigDecimal(1, valor);
            ps.setLong(2, id);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void depositar(Long id, BigDecimal valor){
        String sql = "UPDATE investidor SET saldo_disponivel = saldo_disponivel + ? WHERE id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setBigDecimal(1, valor);
            ps.setLong(2, id);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public BigDecimal consultaSaldo(Long id){
        String sql = "SELECT saldo_disponivel FROM investidor WHERE id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getBigDecimal(1);
                }
            }
            return BigDecimal.ZERO;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
