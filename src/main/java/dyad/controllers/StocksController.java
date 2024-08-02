package dyad.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dyad.dao.StockService;
import dyad.entities.Stock;
import dyad.exceptions.StockNotFoundException;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping(value = "/stocks", produces = { MediaType.TEXT_HTML_VALUE })
public class StocksController {

	@RequestMapping(value="/search", method=RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String searchStockResult(@RequestParam(value="name", required=false) String stock_name, Model model){

		List<Stock> allStocks = stockService.findAll();
		for (Stock a : allStocks){
			if (!(a.getName().toLowerCase().contains(stock_name.toLowerCase()))){
				allStocks.remove(a);
			}
		}
	
		model.addAttribute("stocks", (List<Stock>) allStocks);


		return "stocks/index";
	}

	@Autowired
	private StockService stockService;

	@ExceptionHandler(StockNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String stockNotFoundHandler(StockNotFoundException ex, Model model) {
		model.addAttribute("not_found_id", ex.getId());

		return "stocks/not_found";
	}

	@GetMapping("/{id}")
	public String getStock(@PathVariable("id") long id, Model model) {

		Stock stock = stockService.findById(id).orElseThrow(() -> new StockNotFoundException(id));
		
		model.addAttribute("stock", stock);
		
		return "stocks/show";
	}

	@GetMapping
	public String getAllStocks(Model model) {
		
		  ArrayList<Stock> allStocks = new ArrayList<Stock>();

		  
		  model.addAttribute("allStocks", allStocks);

		 return "stocks/index";
	}
	
    @DeleteMapping(value = "/{id}")
	public String deleteById(@PathVariable("id") long id) {
		stockService.deleteById(id);
		return "redirect:/stocks" ;
	}
}
