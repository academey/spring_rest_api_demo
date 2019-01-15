package me.whiteship.springrestapidemo.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.whiteship.springrestapidemo.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@TestDescription("정상적으로 이벤트를 생성하는 테스트")
	public void createEvent() throws Exception {
		EventDto event = EventDto.builder()
				.name("Spring").description("REST API Development")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 12, 23, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 12, 24, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타트업 팩토리")
				.build();


		mockMvc.perform(post("/api/events")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.accept(MediaTypes.HAL_JSON)
					.content(objectMapper.writeValueAsString(event)
				))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("id").exists())
				.andExpect(header().exists(HttpHeaders.LOCATION))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8"))
				.andExpect(jsonPath("id", Matchers.not(100)))
				.andExpect(jsonPath("free", Matchers.not(true)))
				.andExpect(jsonPath("eventStatus", Matchers.equalTo(EventStatus.DRAFT.name())))
		;
	}

	@Test
	@TestDescription("입력 받을 수 없는 값을 사용한 경우 발생하는 테스트")
	public void createEvent_Bad_Request() throws Exception {
		Event event = Event.builder().name("Spring").description("REST API Development")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 12, 23, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 12, 24, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타트업 팩토리")
				.free(true)
				.id(100)
				.offline(false)
				.eventStatus(EventStatus.PUBLISHED)
				.build();


		mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(event)
				))
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
	}

	@Test
	@TestDescription("입력 값이 비어있는 경우 발생하는 테스트")
	public void createEvent_Bad_Request_empty_input() throws Exception {
		EventDto eventDto = EventDto.builder().build();

		this.mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(eventDto)
				))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@TestDescription("입력 값이 잘못된 경우 발생하는 테스트")
	public void createEvent_Bad_Request_wrong_input() throws Exception {
		EventDto eventDto = EventDto.builder()
				.name("Spring").description("REST API Development")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 12, 26, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 12, 24, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 27, 14, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
				.basePrice(10000)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타트업 팩토리")
				.build();

		this.mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(eventDto)
				))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].objectName" ).exists())
				.andExpect(jsonPath("$[0].defaultMessage" ).exists())
				.andExpect(jsonPath("$[0].code" ).exists())
		;
	}
}
