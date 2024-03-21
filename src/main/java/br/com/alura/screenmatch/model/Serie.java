package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.service.ConsultaChatGPT;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity //declara o Objeto como uma entidade
@Table(name = "series") //coloca o nome da tabela (se não declarado o hibernate irá utilizar o nome da classe alterando de PascalCase para SnakeCase
public class Serie {

    @Id //declara o campo como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) //define o tipo de incremento para a chave
    private Long id;
    @Column(unique = true) // define que o título das séries não podem repetir
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;

    @Enumerated(EnumType.STRING) //declara que o valor desse campo virá de um Enum
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;

    @OneToMany(
            mappedBy = "serie", //define qual atributo de Episodio vai corresponder a serie
            cascade = CascadeType.ALL, //define as operações que serão persistidas em episódio
            fetch = FetchType.EAGER //define que deve buscar os episódios sempre e não esperar um getEpisodios
    )
    private List<Episodio> episodios = new ArrayList<>();

    //construtor padrão - Exigência da JPA
    public Serie() {}

    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        //Tenta converter o valor recebido que é uma string para double, se não conseguir atribui 0.
        this.avaliacao = OptionalDouble.of(Double.parseDouble(dadosSerie.avaliacao())).orElse(0);
        //Chamada do método estático do Enum Catergoria passando apenas o primeiro valor recebido antes da vírgula
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = !dadosSerie.sinopse().equals(" ")
                ? ConsultaChatGPT.obterTraducao(dadosSerie.sinopse().trim())
                : dadosSerie.sinopse();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        //relacionamento bidirecional
        episodios.forEach(e -> e.setSerie(this));
        this.episodios = episodios;
    }

    @Override
    public String toString() {
        return  "genero=" + genero +
                ", titulo='" + titulo + '\'' +
                ", totalTemporadas=" + totalTemporadas +
                ", avaliacao=" + avaliacao +
                ", atores='" + atores + '\'' +
                ", poster='" + poster + '\'' +
                ", sinopse='" + sinopse + '\'' +
                ", episodios='" + episodios + '\'';
    }
}
