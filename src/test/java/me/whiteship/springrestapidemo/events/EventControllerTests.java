package me.whiteship.springrestapidemo.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.whiteship.springrestapidemo.common.RestDocsConfiguration;
import me.whiteship.springrestapidemo.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
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
				.beginEventDateTime(LocalDateTime.of(2018, 12, 25, 14, 21))
				.endEventDateTime(LocalDateTime.of(2018, 12, 26, 14, 21))
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
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("free", Matchers.equalTo(false)))
				.andExpect(jsonPath("offline", Matchers.equalTo(true)))
				.andExpect(jsonPath("eventStatus", Matchers.equalTo(EventStatus.DRAFT.name())))
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.query-events").exists())
				.andExpect(jsonPath("_links.update-event").exists())
				.andDo(document("create-event",
						links(
							linkWithRel("self").description("Link to self"),
							linkWithRel("query-events").description("Link to query events"),
							linkWithRel("update-event").description("Link to update event")
						),
						requestHeaders(
								headerWithName(HttpHeaders.ACCEPT).description("accept Header"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
						),
						requestFields(
								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("description of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
								fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
								fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
								fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
								fieldWithPath("location").description("location of new event"),
								fieldWithPath("basePrice").description("basePrice of new event"),
								fieldWithPath("maxPrice").description("maxPrice of new event"),
								fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
						),
						responseHeaders(
								headerWithName(HttpHeaders.LOCATION).description("response header location"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("response header content type")
						),
						responseFields(
								fieldWithPath("id").description("Id of new event"),
								fieldWithPath("free").description("It tells is  this event is free or not"),
								fieldWithPath("offline").description("It tells is this event is offline or not"),

								fieldWithPath("_links.self.href").description("Link to self"),
								fieldWithPath("_links.query-events.href").description("Link to query events"),
								fieldWithPath("_links.update-event.href").description("Link to update event"),

								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("description of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
								fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
								fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
								fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
								fieldWithPath("location").description("location of new event"),
								fieldWithPath("basePrice").description("basePrice of new event"),
								fieldWithPath("maxPrice").description("maxPrice of new event"),
								fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
								fieldWithPath("eventStatus").description("eventStatus of new event")
						)
				))
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
