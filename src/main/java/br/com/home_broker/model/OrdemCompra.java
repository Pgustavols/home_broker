package br.com.home_broker.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class OrdemCompra {
    private Long id;
    private Long idInvestidor;
    private Long idAcao;
    private Long quantidade;
    private BigDecimal precoTotal;
    private LocalDateTime dataCompra;
    private String ticker;


    public OrdemCompra(Long idInvestidor, Long idAcao, Long quantidade, BigDecimal precoTotal, LocalDateTime dataCompra) {
        this.idInvestidor = idInvestidor;
        this.idAcao = idAcao;
        this.quantidade = quantidade;
        this.precoTotal = precoTotal;
        this.dataCompra = dataCompra;
    }

    public OrdemCompra(String ticker, Long quantidade, BigDecimal precoTotal) {
        this.ticker = ticker;
        this.quantidade = quantidade;
        this.precoTotal = precoTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdemCompra that = (OrdemCompra) o;
        return quantidade == that.quantidade && Objects.equals(id, that.id) && Objects.equals(idInvestidor, that.idInvestidor) && Objects.equals(idAcao, that.idAcao) && Objects.equals(precoTotal, that.precoTotal) && Objects.equals(dataCompra, that.dataCompra);
    }



    public Long getIdInvestidor() {
        return idInvestidor;
    }

    @Override
    public String toString() {
        return "OrdemCompra{" +
                "ticker=" + ticker +
                ", quantidade=" + quantidade +
                ", Preço Total R$='" + precoTotal + '\'' +
                '}';
    }

    public Long getIdAcao() {
        return idAcao;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoTotal() {
        return precoTotal;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idInvestidor, idAcao, quantidade, precoTotal, dataCompra);
    }
}
