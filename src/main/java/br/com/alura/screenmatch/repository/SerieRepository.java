package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    //Derived querys
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    //numero maximo de temporadas e avaliação maior igual valor
    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer totalTemporadas, Double avaliacao);


    //SQL nativo - depende do banco de dados
    //@Query(valure = "select * from series where series.totalTemporadas <= :totalTemporadas and series.avaliacao >= :avaliacao", nativeQuery = true)

    //JPQL -  Abstrai o banco de dados e a JPA trabalha com o objeto
    @Query("select s from Serie s where s.totalTemporadas <= :totalTemporadas and s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao(Integer totalTemporadas, Double avaliacao);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nomeEpisodio%")
    List<Episodio> espisodiosPorTrecho(String nomeEpisodio);


    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie  ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :anoLancamento")
    List<Episodio> buscarEpisidiosPorSerieAposData(Serie serie, int anoLancamento);
}
