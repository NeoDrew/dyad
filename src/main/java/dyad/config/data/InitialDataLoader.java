package dyad.config.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import dyad.dao.StockService;
import dyad.entities.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
@Profile("default")
public class InitialDataLoader {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	@Autowired
	private StockService stockService;


	@Bean
	CommandLineRunner initDatabase() {
		return args -> {

			if (stockService.count() > 0) {
				log.info("Database already populated with stocks. Skipping stock initialization.");
			} else {
				Stock stock1=new Stock();
				stock1.setCode("SPN");
				stock1.setName("S&P500");
				stock1.setExchange("London Stock Exchange");
				log.info("Initialize stock1 :" + stockService.save(stock1));
				
				Stock stock2=new Stock();
				stock1.setCode("APP");
				stock1.setName("Apple Inc.");
				stock1.setExchange("New York Stock Exchange");
				log.info("Initialize stock2 : "+ stockService.save(stock2));
				
				Stock stock3=new Stock();
				stock1.setCode("TSLA");
				stock1.setName("Tesla");
				stock1.setExchange("London Stock Exchange");
				log.info("Initialize stock3 : "+ stockService.save(stock3));// Build and save initial stocks here.
			}
		};
	}
}
