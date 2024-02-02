package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey=a3c9a152";

    public void exibeMenu(){
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine().replace(" ", "+");
        var json = consumoApi.obterDados(ENDERECO + nomeSerie + APIKEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

		List<DadosTemporada> listaTemporadas = new ArrayList<>();
		for (int i = 1; i <= dadosSerie.totalTemporadas() ; i++) {
			json = consumoApi.obterDados(ENDERECO + nomeSerie +"&season=" + i + APIKEY );
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			listaTemporadas.add(dadosTemporada);
		}

		listaTemporadas.forEach(System.out::println);

//        for (DadosTemporada temporada: listaTemporadas){
//            List<DadosEpisodio> episodiosTemporada = temporada.episodios();
//            for (DadosEpisodio episodio: episodiosTemporada){
//                System.out.println(episodio.titulo());
//            }
//        }

        //listaTemporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = listaTemporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());
                //.toList(); //Lista imutável, não consegue adicionar elementos

        //dadosEpisodios.add(new DadosEpisodio("teste", 3, "7.0", "2021"));

        System.out.println("Top 5 episódios");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);


        List<Episodio> episodios = listaTemporadas.stream()
                .flatMap(t -> t.episodios().stream()
                    .map(d -> new Episodio(t.numeroTemporada(), d))
                ).collect(Collectors.toList());
        
        episodios.forEach(System.out::println);

        System.out.println("A partir de qual ano deseja ver os episódios?");
        var ano = leitura.nextInt();
        leitura.nextLine();

        LocalDate dataDeBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataDeBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                "Episódio: " + e.getTitulo() +
                                "Data lançamento: " + e.getDataLancamento().format(formatoData)
                ));

        /* Exemplo de stream e como podemos utilizar
        * Operações que alteram a lista (geram outra versão da lista) são as operações intermediárias
        * e Operações que interagem com a lista (mas não geram outra) são as operações finais*/
//        List<String> nomes = Arrays.asList("Luma", "Ed", "Koga", "Liz", "Jupiter", "Troid");
//
//        nomes.stream()
//                .sorted() //intermediaria
//                .limit(4) //intermediaria
//                .filter(n -> n.startsWith("K")) //intermediaria
//                .map(String::toUpperCase) //intermediaria
//                .forEach(System.out::println); //final
    }
}
