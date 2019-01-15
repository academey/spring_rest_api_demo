package me.whiteship.springrestapidemo.events;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value ="/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {
	@Autowired
	EventRepository eventRepository;

	@Autowired
	ModelMapper modelMapper;

	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().build();
		}
		Event event = modelMapper.map(eventDto, Event.class);
		Event newEvent = eventRepository.save(event);
		URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
		return ResponseEntity.created(createdUri).body(event);
	}
}
