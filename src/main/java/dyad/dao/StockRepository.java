package dyad.dao;

import org.springframework.data.repository.CrudRepository;

import dyad.entities.Stock;

import org.springframework.data.domain.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Date;

public interface StockRepository extends CrudRepository<Stock, Long>{

	public Iterable<Stock> findAll(Sort sort);

	public Iterable<Stock> findAllByNameContaining(String stock_name);

	public List<Stock> findAllByNameIgnoreCaseContainingOrderByNameAsc(String name);
}