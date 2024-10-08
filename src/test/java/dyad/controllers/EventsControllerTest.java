package dyad.controllers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import dyad.config.Security;
import dyad.dao.StockService;
import dyad.entities.Stock;

import org.mockito.ArgumentCaptor;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StocksController.class)
@Import(Security.class)
public class EventsControllerTest {

	@Autowired
	private MockMvc mvc;

	@Mock
	private Stock event;

	@MockBean
	private StockService eventService;

	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Stock>emptyList());

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
		//verify(venueService).findAll();
		verifyNoInteractions(event);
	}

	@Test
	public void getIndexWithPreviousEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Stock>singletonList(event));
		when(event.getDate()).thenReturn(LocalDate.now());
		when(event.getTime()).thenReturn(LocalTime.now());
		

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
	}
	

	@Test
	public void getEventNotFound() throws Exception {
		mvc.perform(get("/events/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found")).andExpect(handler().methodName("getEvent"));
	}
	@Test
	public void testDeleteEvent() throws Exception{

		mvc.perform(delete("/events/1").with(user("Rob").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/events"))
		.andExpect(handler().methodName("deleteById"));
		
		verify(eventService).deleteById(1);
		
	}
	
	@Test
	public void updateEvent() throws Exception {
		
		when(event.getName()).thenReturn("EventName");
		when(eventService.findById(6)).thenReturn(Optional.of(event));

		mvc.perform(get("/events/updateEvent?id=6").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(view().name("events/updateEvent.html"))
				.andExpect(handler().methodName("updateEvent"));

		verify(eventService).findById(6);
	}
	
	@Test
	public void getNewEvent() throws Exception {
		mvc.perform(get("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk()).andExpect(view().name("events/new"))
				.andExpect(handler().methodName("newEvent"));
	}
	
	@Test
	 public void createEvent() throws Exception {
	  ArgumentCaptor<Stock> arg = ArgumentCaptor.forClass(Stock.class);
	  when(eventService.save(any(Stock.class))).then(returnsFirstArg());
	  
	  
	  
	  mvc.perform(post("/events/createEvent").with(user("Rob").roles(Security.ADMIN_ROLE))
	    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    .param("id", "1")
	    .param("name", "Test2")
	    .param("venue.id","2")
	    .param("date", "2099-10-23")
	    .param("time", "12:50")
	    .param("description", "AAA")
	    .accept(MediaType.TEXT_HTML).with(csrf()))
	    .andExpect(view().name("redirect:/events"))
	    .andExpect(handler().methodName("createEvent"))
	    .andExpect(flash().attributeExists("ok_message"));
	  verify(eventService).save(arg.capture());
	  assertThat("Test2", equalTo(arg.getValue().getName()));
	  assertThat(1L, equalTo(arg.getValue().getId()));
	 }
	
	@Test
	public void postEventNoAuth() throws Exception {
		mvc.perform(post("/events/createEvent").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "1")
			    .param("name", "Test2")
			    .param("venue","2")
			    .param("date", "2099-10-23")
			    .param("time", "12:50")
			    .param("description", "AAA")
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
				.andExpect(header().string("Location", endsWith("/sign-in")));

		verify(eventService, never()).save(any(Stock.class));
	}
	
	@Test
	public void postEventNoCsrf() throws Exception {
		mvc.perform(post("/events/createEvent").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "1")
			    .param("name", "Test2")
			    .param("venue","2")
			    .param("date", "2099-10-23")
			    .param("time", "12:50")
			    .param("description", "AAA")
				.accept(MediaType.TEXT_HTML)).andExpect(status().isForbidden());

		verify(eventService, never()).save(any(Stock.class));
	}
	
	@Test
	public void createEventwithError() throws Exception {
		
	  ArgumentCaptor<Stock> arg = ArgumentCaptor.forClass(Stock.class);
	  when(eventService.save(any(Stock.class))).then(returnsFirstArg());	  	  
	  
	  mvc.perform(post("/events/createEvent").with(user("Rob").roles(Security.ADMIN_ROLE))
	    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    .param("id", "1")
	    .param("name", "Test2")
	    .param("venue","2")
	    .param("date", "2000-10-23")
	    .param("time", "12:50")
	    .param("description", "AAA")
	    .accept(MediaType.TEXT_HTML).with(csrf()))
	    .andExpect(view().name("events/new"))
	    .andExpect(handler().methodName("createEvent"));
	  
	  verify(eventService, never()).save(arg.capture());
	 }
	
	@Test
	public void createEventwithNoName() throws Exception {
		
	  ArgumentCaptor<Stock> arg = ArgumentCaptor.forClass(Stock.class);
	  when(eventService.save(any(Stock.class))).then(returnsFirstArg());	  	  
	  
	  mvc.perform(post("/events/createEvent").with(user("Rob").roles(Security.ADMIN_ROLE))
	    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    .param("id", "1")
	    .param("venue","2")
	    .param("date", "2000-10-23")
	    .param("time", "12:50")
	    .param("description", "AAA")
	    .accept(MediaType.TEXT_HTML).with(csrf()))
	    .andExpect(view().name("events/new"))
	    .andExpect(handler().methodName("createEvent"));
	  
	  verify(eventService, never()).save(arg.capture());
	 }
	
	@Test
	public void createEventwithLongName() throws Exception {
		
	  ArgumentCaptor<Stock> arg = ArgumentCaptor.forClass(Stock.class);
	  when(eventService.save(any(Stock.class))).then(returnsFirstArg());	  	  
	  mvc.perform(post("/events/createEvent").with(user("Rob").roles(Security.ADMIN_ROLE))
	    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    .param("id", "1")
	    .param("name", new String(new char[300]).replace('\0', 'a'))
	    .param("venue.id","2")
	    .param("date", "2099-10-23")
	    .param("time", "12:50")
	    .param("description", "AAA")

	    .accept(MediaType.TEXT_HTML).with(csrf()))
	    .andExpect(view().name("events/new"))
	    .andExpect(handler().methodName("createEvent"));
	  
	  verify(eventService, never()).save(arg.capture());
	 }
	
	
	@Test
	public void createEventwithInvalidTime() throws Exception {
		
	  ArgumentCaptor<Stock> arg = ArgumentCaptor.forClass(Stock.class);
	  when(eventService.save(any(Stock.class))).then(returnsFirstArg());	  	  
	  mvc.perform(post("/events/createEvent").with(user("Rob").roles(Security.ADMIN_ROLE))
	    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    .param("id", "1")
	    .param("name", new String(new char[300]).replace('\0', 'a'))
	    .param("venue.id","2")
	    .param("date", "2099-10-23")
	    .param("time", "55:99")
	    .param("description", "AAA")

	    .accept(MediaType.TEXT_HTML).with(csrf()))
	    .andExpect(view().name("events/new"))
	    .andExpect(handler().methodName("createEvent"));
	  
	  verify(eventService, never()).save(arg.capture());
	 }
	
	@Test
	 public void createEventwithNoDate() throws Exception {
		
	  ArgumentCaptor<Stock> arg = ArgumentCaptor.forClass(Stock.class);
	  when(eventService.save(any(Stock.class))).then(returnsFirstArg());	  	  
	  
	  mvc.perform(post("/events/createEvent").with(user("Rob").roles(Security.ADMIN_ROLE))
	    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    .param("id", "1")
	    .param("name", "Test2")
	    .param("venue","2")
	    .param("time", "12:50")
	    .param("description", "AAA")
	    .accept(MediaType.TEXT_HTML).with(csrf()))
	    .andExpect(view().name("events/new"))
	    .andExpect(handler().methodName("createEvent"));
	  
	  verify(eventService, never()).save(arg.capture());
	 }
	
	@Test
	public void getEvent() throws Exception {
		when(eventService.findById(1)).thenReturn(Optional.of(event));

		mvc.perform(get("/events/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/show")).andExpect(handler().methodName("getEvent"));

	}

	@Test
	public void updateEventInvalidData() throws Exception
	{
		 when(eventService.save(any(Stock.class))).then(returnsFirstArg());	  	  
		when(eventService.findById(0)).thenReturn(null);
		
		mvc.perform(post("/events/update").with(user("Rob").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML).with(csrf())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.param("id", "1")
			.param("date", "2050-01-01")
			.param("id", "2")
			.param("time", "01:00")
			.param("name", "test")
			.param("description", ""))
			.andExpect(status().is2xxSuccessful());
		
		verify(eventService, never()).save(any(Stock.class));
	}
	
	@Test
	public void updateEventValidData() throws Exception
	{
		 when(eventService.save(any(Stock.class))).then(returnsFirstArg());	  	  
		when(eventService.findById(0)).thenReturn(null);
		
		mvc.perform(post("/events/update").with(user("Rob").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML).with(csrf())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.param("id", "1")
			.param("date", "2050-01-01")
			.param("venue.id", "2")
			.param("time", "01:00")
			.param("name", "test")
			.param("description", ""))
		    .andExpect(flash().attributeExists("ok_message"))
			.andExpect(view().name("redirect:/events"));
		
		verify(eventService).save(any(Stock.class));
	}
	
	
	@Test
	public void updateEventInvalid() throws Exception
	{
		when(eventService.findById(0)).thenReturn(null);
		
		mvc.perform(post("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML).with(csrf())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.param("date", "2050-01-01")
			.param("time", "01:00")
			.param("name", "test")
			.param("description", ""))
			.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void updateEventNoAuth() throws Exception {
		mvc.perform(post("/events/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.param("name", "test")
			.param("id", "1")
			.param("date", "2050-01-01")
			.param("time","01:00")
			.param("Venue_id", "1")
			.param("description", "test")
			.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
			.andExpect(header().string("Location", endsWith("/sign-in")));

		verify(eventService, never()).save(event);
	}

	@Test
	public void updateEventBadRole() throws Exception {
		mvc.perform(post("/events/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.with(user("Rob").roles("ATTENDEES"))
			.param("name", "test")
			.param("id", "1")
			.param("date", "2050-01-01")
			.param("time","01:00")
			.param("Venue_id", "1")
			.param("description", "test")
			.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isForbidden());

		verify(eventService, never()).save(event);
	}	
	
	@Test
	public void updateEventNoCsrf() throws Exception {
		mvc.perform(post("/events/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.with(user("Rob").roles(Security.ADMIN_ROLE))
			.param("name", "test")
			.param("id", "1")
			.param("date", "2050-01-01")
			.param("time","01:00")
			.param("Venue_id", "1")
			.param("description", "test")
			.accept(MediaType.TEXT_HTML))
			.andExpect(status().isForbidden());

		verify(eventService, never()).save(event);
	}
	
	@Test
	public void searchTestWithNoEvents() throws Exception {		
		when(event.getName()).thenReturn("EventName");
		when(eventService.findUpcomingEvents()).thenReturn(Collections.<Stock>emptyList());
		when(eventService.findPastEvents()).thenReturn(Collections.<Stock>emptyList());
		
		mvc.perform(get("/events/search?name=Engi").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(view().name("events/index"))
				.andExpect(handler().methodName("searchEventResult"));

		verify(eventService).findUpcomingEvents();
		verify(eventService).findPastEvents();
	}
	
	@Test
	public void searchTestWithEvents() throws Exception {
		Stock event1=new Stock();
		LocalDate localdate1 = LocalDate.of(2022, 03, 01);
		LocalTime localtime1 = LocalTime.of(16, 00);
		event1.setDate(localdate1);
		event1.setName("COMP23412 Lab");
		event1.setTime(localtime1);
		
		Stock event2=new Stock();
		LocalDate localdate2 = LocalDate.of(2022, 03, 03);
		LocalTime localtime2 = LocalTime.of(12, 00);
		event2.setDate(localdate2);
		event2.setName("PASS");
		event2.setTime(localtime2);
		
		List<Stock> upcoming_event_list = new ArrayList<Stock>();
		upcoming_event_list.add(event1);
		upcoming_event_list.add(event2);
		
		List<Stock> past_event_list = new ArrayList<Stock>();
		past_event_list.add(event1);
		past_event_list.add(event2);
		
		when(event.getName()).thenReturn("EventName");
		when(eventService.findUpcomingEvents()).thenReturn(upcoming_event_list);
		when(eventService.findPastEvents()).thenReturn(past_event_list);

		mvc.perform(get("/events/search?name=Engi").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(view().name("events/index"))
				.andExpect(handler().methodName("searchEventResult"));

		verify(eventService).findUpcomingEvents();
		verify(eventService).findPastEvents();
	}

	@Test
	public void searchTestSuccessfulSearch() throws Exception {

		Stock event1=new Stock();
		LocalDate localdate1 = LocalDate.of(2022, 03, 01);
		LocalTime localtime1 = LocalTime.of(16, 00);
		event1.setDate(localdate1);
		event1.setName("COMP23412 Lab");
		event1.setTime(localtime1);
		
		Stock event2=new Stock();
		LocalDate localdate2 = LocalDate.of(2022, 03, 03);
		LocalTime localtime2 = LocalTime.of(12, 00);
		event2.setDate(localdate2);
		event2.setName("PASS");
		event2.setTime(localtime2);
		
		List<Stock> upcoming_event_list = new ArrayList<Stock>();
		upcoming_event_list.add(event1);
		upcoming_event_list.add(event2);
		
		List<Stock> past_event_list = new ArrayList<Stock>();
		past_event_list.add(event1);
		past_event_list.add(event2);
		
		when(event.getName()).thenReturn("EventName");
		when(eventService.findUpcomingEvents()).thenReturn(upcoming_event_list);
		when(eventService.findPastEvents()).thenReturn(past_event_list);

		mvc.perform(get("/events/search?name=COMP").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(view().name("events/index"))
				.andExpect(handler().methodName("searchEventResult"));

		verify(eventService).findUpcomingEvents();
		verify(eventService).findPastEvents();
	}
}
