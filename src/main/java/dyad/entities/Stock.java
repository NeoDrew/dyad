package dyad.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Future;


import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="stocks")
public class Stock {
    
	@Id
	@GeneratedValue
	private long id;

	@NotEmpty(message = "Stock must have a name")
	@Size(max = 256, message = "Stock name must have < 256 characters")
	private String name;

	@NotEmpty(message = "Stock must have a code")
	@Size(max = 8, message = "Stock code must have < 8 characters")
	private String code;

	@NotEmpty(message = "Stock must have an exchange")
	@Size(max = 64, message = "Stock exchange must have < 64 characters")
	private String exchange;
	
	@Size(max = 500, message = "Stock description must have < 500 characters")
	private String description;
	

	public Stock() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getExchange() {
		return code;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
