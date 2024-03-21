package br.com.alura.screenmatch.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "episodios")
public class Episodio {

    @Id //declara o campo como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) //define o tipo de incremento para a chave
    private Long id;
    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double avaliacao;
    private LocalDate dataLancamento;

    @ManyToOne
//    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    public Episodio() {}

    public Episodio(Integer numeroTemporada, DadosEpisodio d) {
        this.temporada = numeroTemporada;
        this.titulo = d.titulo();
        this.numeroEpisodio = d.numeroEpisodio();
        try {
            this.avaliacao = Double.valueOf(d.avaliacao());
        } catch (NumberFormatException e) {
            this.avaliacao = .0;
        }

        try {
            this.dataLancamento = LocalDate.parse(d.dataLancamento());
        } catch (DateTimeParseException e) {
            this.dataLancamento = null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    @Override
    public String toString() {
        return  "temporada=" + temporada +
                ", titulo='" + titulo + '\'' +
                ", numeroEpisodio=" + numeroEpisodio +
                ", avaliacao=" + avaliacao +
                ", dataLancamento=" + dataLancamento;
    }
}
