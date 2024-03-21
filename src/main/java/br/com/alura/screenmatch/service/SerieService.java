package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey="+System.getenv("OMDB_API_KEY");

    public List<SerieDTO> obterSeries() {
        return converteListaSerieParaDTO(serieRepository.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteListaSerieParaDTO(serieRepository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos() {
        return converteListaSerieParaDTO(serieRepository.encontrarEpisodiosMaisRecentes());
    }
    public SerieDTO obterDadosSeries(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()) {
            if (serie.get().getEpisodios().isEmpty()) {
                buscarEpisodios(serie.get());
            }
            return serieToDto(serie.get());
        }
        return null;
    }

    public List<EpisodioDTO> obterTodasAsTemporadas(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()) {
            return converteListaEpisodiosParaDTO(serie.get().getEpisodios());
        }
        return null;
    }

    public List<EpisodioDTO> obterDadosTemporada(Long id, Integer numeroTemporada) {
        return converteListaEpisodiosParaDTO(serieRepository.obterEpisodiosPorTemporada(id, numeroTemporada));
    }

    public List<SerieDTO> obterSeriesPorCategoria(String categoria) {
        return converteListaSerieParaDTO(serieRepository.findByGenero(Categoria.fromPortugues(categoria)));
    }

    public List<EpisodioDTO> obterTopEpisodiosSerie(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()) {
            return converteListaEpisodiosParaDTO(serieRepository.topEpisodiosPorSerie(serie.get()));
        }
        return null;
    }

    private List<SerieDTO> converteListaSerieParaDTO(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(
                        s.getId(),
                        s.getTitulo(),
                        s.getTotalTemporadas(),
                        s.getAvaliacao(),
                        s.getGenero(),
                        s.getAtores(),
                        s.getPoster(),
                        s.getSinopse()))
                .collect(Collectors.toList());
    }

    private SerieDTO serieToDto(Serie serie) {
        return new SerieDTO(
                serie.getId(),
                serie.getTitulo(),
                serie.getTotalTemporadas(),
                serie.getAvaliacao(),
                serie.getGenero(),
                serie.getAtores(),
                serie.getPoster(),
                serie.getSinopse());
    }

    private List<EpisodioDTO> converteListaEpisodiosParaDTO(List<Episodio> episodios) {
        return episodios.stream()
                .map(e -> new EpisodioDTO(
                        e.getTemporada(),
                        e.getNumeroEpisodio(),
                        e.getTitulo()))
                .collect(Collectors.toList());
    }

    //Rotina criada para buscar os episódios caso a série não tenha episódios na base
    private void buscarEpisodios(Serie serie) {

        var tituloSerie = serie.getTitulo().replace(" ", "+");

        List<DadosTemporada> listaTemporadas = new ArrayList<>();

        for (int i = 1; i <= serie.getTotalTemporadas(); i++) {
            var json = consumoApi.obterDados(ENDERECO + tituloSerie + "&season=" + i + APIKEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            listaTemporadas.add(dadosTemporada);
        }
        List<Episodio> episodios = listaTemporadas.stream()
                .flatMap(d -> d.episodios().stream()
                        .map(e -> new Episodio(d.numeroTemporada(), e)))
                .collect(Collectors.toList());
        serie.setEpisodios(episodios);
        serieRepository.save(serie);
    }
}
