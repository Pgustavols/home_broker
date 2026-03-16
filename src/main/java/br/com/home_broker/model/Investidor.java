package br.com.home_broker.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Investidor {

    private Long id;
    private String nome;
    private String cpf;
    private BigDecimal saldoDisponivel;


    public Investidor(String nome, String cpf, BigDecimal saldo_disponivel) {
        this.nome = nome;
        this.cpf = cpf;
        this.saldoDisponivel = saldo_disponivel;
    }

    public Investidor(Long id, String nome, String cpf, BigDecimal saldoDisponivel) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.saldoDisponivel = saldoDisponivel;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getSaldoDisponivel() {
        return saldoDisponivel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Investidor that = (Investidor) o;
        return Objects.equals(id, that.id) && Objects.equals(cpf, that.cpf);
    }

    @Override
    public String toString() {
        return "Investidor{" +
                "nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", saldo_disponivel=" + saldoDisponivel +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cpf);
    }
}
