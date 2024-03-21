package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @GetMapping
    public List<SerieDTO> obterSeries() {
        return serieService.obterSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obeterTop5Series() {
        return serieService.obterTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterLancamentos() {
        return serieService.obterLancamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO obterDadosSeries(@PathVariable Long id) {
        return serieService.obterDadosSeries(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obterTodasAsTemporadas(@PathVariable Long id) {
        return serieService.obterTodasAsTemporadas(id);
    }

    @GetMapping("/{id}/temporadas/{numeroTemporada}")
    public List<EpisodioDTO> obterDadosTemporada(
            @PathVariable Long id,
            @PathVariable Integer numeroTemporada) {
        return serieService.obterDadosTemporada(id, numeroTemporada);
    }

    @GetMapping("/categoria/{categoria}")
    //public List<SerieDTO> obterSeriesPorCategoria(@PathVariable("categoria") String genero)
    //A anotação do @PathVariable também funciona assim caso não queria utilizar o mesmo nome do parâmetro da URL
    //e o de entrada do método
    public List<SerieDTO> obterSeriesPorCategoria(@PathVariable String categoria) {
        return serieService.obterSeriesPorCategoria(categoria);
    }

    @GetMapping("/{id}/temporadas/top")
    public List<EpisodioDTO> obterTopEpisodiosSerie(
            @PathVariable Long id) {
        return serieService.obterTopEpisodiosSerie(id);
    }
}
