package dyad.dao;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import dyad.entities.Stock;

@Service
public class StockServiceImpl implements StockService {

	private final static Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

	private final static String DATA = "data/Stocks.json";
	
	@Autowired
	private StockRepository stockRepository;

	@Override
	public Iterable<Stock> findAllByNameContaining(String stock_name){
		return stockRepository.findAllByNameContaining(stock_name);
	}

	@Override
	public long count() {
		return stockRepository.count();
	}

	@Override
	public List<Stock> findAll() {
		return (List<Stock>) stockRepository.findAll(Sort.by("date").ascending().and(Sort.by("name").ascending()));
	}

	@Override
	public List<Stock> findAllByNameIgnoreCaseContainingOrderByNameAsc(String stock_name) {
		return stockRepository.findAllByNameIgnoreCaseContainingOrderByNameAsc(stock_name);
	}

	@Override
	public Stock save(Stock stock) {
		return stockRepository.save(stock);
	}
	
	@Override
	public void delete(Stock stock) {
		stockRepository.delete(stock);
	}

	
	@Override
	public void deleteById(long id) {
		stockRepository.deleteById(id);
	}
	
	@Override
    public Optional<Stock> findById(long id){
    	return stockRepository.findById(id);
    }
}
