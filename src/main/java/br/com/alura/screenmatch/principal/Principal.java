package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey="+System.getenv("OMDB_API_KEY");
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository serieRepository;

    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while ( opcao != 0 ) {
            var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries buscadas
                4 - Buscar série por titulo
                5 - Burcar séries por ator
                6 - Top 5 séries
                7 - Buscar séries por categoria
                8 - Buscar por quantidade de temporadas
                9 - Buscar episódio por nome
                10 - Top 5 episódios
                11 - Buscar episódios a partir de uma data
                
                0 - Sair
                """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerie();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    buscarSeriePorTemporada();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarTopEspisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosAposData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }

    }

    private void buscarSerie() {
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine().replace(" ", "+");
        Optional<Serie> serie = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);
        if ( serie.isPresent() ) {
            System.out.println(serie.get());
        } else {
            buscarSerieWeb(nomeSerie);
        }

    }

    private void buscarSerieWeb(String nomeSerie) {
        DadosSerie dados = getDadosSerie(nomeSerie);
//        dadosSeries.add(dados);
        Serie serie = new Serie(dados);
        serieRepository.save(serie);
        System.out.println(serie);
    }

    private DadosSerie getDadosSerie(String nomeSerie) {
        var json = consumoApi.obterDados(ENDERECO + nomeSerie + APIKEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série para listar os episódios");
        var nomeSerie = leitura.nextLine();

//        Optional<Serie> serie = series.stream()
//                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
//                .findFirst();

        Optional<Serie> serie = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        if ( serie.isPresent() ) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> listaTemporadas = new ArrayList<>();

            var tituloSerie = serieEncontrada.getTitulo().replace(" ", "+");

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas() ; i++) {
                var json = consumoApi.obterDados(ENDERECO + tituloSerie +"&season=" + i + APIKEY );
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                listaTemporadas.add(dadosTemporada);
            }
            listaTemporadas.forEach(System.out::println);

            List<Episodio> episodios = listaTemporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numeroTemporada(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            serieRepository.save(serieEncontrada);
        } else {
            System.out.println("Serie não encontrada");
        }


    }

    private void listarSeriesBuscadas() {
//        List<Serie> series = new ArrayList<>()
//        series = dadosSeries.stream()
//                        .map(d -> new Serie(d))
//                        .collect(Collectors.toList());
//
        series = serieRepository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série para listar os episódios");
        var nomeSerie = leitura.nextLine();
        serieBuscada = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        if ( serieBuscada.isPresent() ) {
            System.out.println(serieBuscada);
        } else {
            System.out.println("Serie não encontrada");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome de um ator para listar as séries");
        var nomeAtor = leitura.nextLine();
        System.out.println("Com avaliação a partir de quanto?");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesPorAtor =
                serieRepository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(
                        nomeAtor.toLowerCase(),
                        avaliacao
                );
        System.out.println("Series em que " + nomeAtor + " participou");
        seriesPorAtor.forEach(serie -> System.out.println(serie.getTitulo() + " - Avaliação: " + serie.getAvaliacao()));
    }

    private void buscarTop5Series() {
        System.out.println("5 séries mais bem avaliadas:");
        List<Serie> seriesTop5 = serieRepository.findTop5ByOrderByAvaliacaoDesc();
        seriesTop5.forEach(serie -> System.out.println(serie.getTitulo() + " - Avaliação: " + serie.getAvaliacao()));
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Escolha qual categoria buscar");
        var categoriaEscolhida = leitura.nextLine();
        var categoria = Categoria.fromPortugues(categoriaEscolhida);
        List<Serie> seriesPorCategoria = serieRepository.findByGenero(categoria);
        System.out.println("Séries do gênero " + categoriaEscolhida);
        seriesPorCategoria.forEach(System.out::println);
    }


    private void buscarSeriePorTemporada() {
        System.out.println("Digite até quantas temporadas para listar as séries");
        var temporadas = leitura.nextInt();
        System.out.println("Com avaliação a partir de quanto?");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesPorAtor =
                serieRepository.seriesPorTemporadaEAvaliacao(
                        temporadas,
                        avaliacao
                );
        System.out.println("Series com até " + temporadas + " temporadas");
        seriesPorAtor.forEach(serie -> System.out.println(
                serie.getTitulo() +
                        " - Avaliação: " + serie.getAvaliacao() +
                        " - Temporadas: " + serie.getTotalTemporadas()));
    }


    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite o nome(ou parte) do episódio para busca");
        var nomeEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = serieRepository.espisodiosPorTrecho(nomeEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                e.getSerie().getTitulo(),
                        e.getTemporada(),
                        e.getNumeroEpisodio(),
                        e.getTitulo()));
    }


    private void buscarTopEspisodiosPorSerie() {
        buscarSeriePorTitulo();
        if ( serieBuscada.isPresent() ) {
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = serieRepository.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s - Avaliação: %s\n",
                            e.getSerie().getTitulo(),
                            e.getTemporada(),
                            e.getNumeroEpisodio(),
                            e.getTitulo(),
                            e.getAvaliacao()));
        }
    }


    private void buscarEpisodiosAposData() {
        buscarSeriePorTitulo();
        if ( serieBuscada.isPresent() ) {
            System.out.println("Digite o ano limite de lançamento");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();
            Serie serie = serieBuscada.get();
            List<Episodio> episodiosAno = serieRepository.buscarEpisidiosPorSerieAposData(serie, anoLancamento);
            episodiosAno.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s - Data de Lançamento: %s\n",
                            e.getSerie().getTitulo(),
                            e.getTemporada(),
                            e.getNumeroEpisodio(),
                            e.getTitulo(),
                            e.getDataLancamento()));
        }
    }
}

