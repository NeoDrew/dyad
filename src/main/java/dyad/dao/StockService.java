package dyad.dao;

import java.util.List;
import java.util.Optional;

import dyad.entities.Stock;

public interface StockService {

	public long count();

	public List<Stock> findAll();
	
	public Stock save(Stock stock);

	public Iterable<Stock> findAllByNameContaining(String stock_name);
	
	public void delete(Stock stock);
	
	public void deleteById(long id);
	
	public Optional<Stock> findById(long id);

	public List<Stock> findAllByNameIgnoreCaseContainingOrderByNameAsc(String name);
}
