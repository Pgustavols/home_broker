package br.com.home_broker.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Acao {
    private Long id;
    private String ticker;
    private BigDecimal preco_atual;

    public Acao(String ticker, BigDecimal preco_atual) {
        this.ticker = ticker;
        this.preco_atual = preco_atual;
    }
    public Acao(Long id, String ticker, BigDecimal preco_atual) {
        this.id = id;
        this.ticker = ticker;
        this.preco_atual = preco_atual;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Acao acao = (Acao) o;
        return Objects.equals(id, acao.id) && Objects.equals(ticker, acao.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ticker);
    }

    public String getTicker() {
        return ticker;
    }

    public BigDecimal getPreco_atual() {
        return preco_atual;
    }

    @Override
    public String toString() {
        return "Acao{" +
                "id=" + id +
                ", ticker='" + ticker + '\'' +
                ", preco_atual=" + preco_atual +
                '}';
    }
}
