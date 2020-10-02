package com.kakaopay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.dto.SprinklingReqDto;
import com.kakaopay.service.ReceivingService;
import com.kakaopay.service.SprinklingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
class SprinklingControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private SprinklingService sprinklingService;
  @Autowired private ReceivingService receivingService;

  @Test
  @DisplayName("뿌리기를 요청하고 token을 반환 받음")
  void SprinklingTest() throws Exception {

    // Given
    SprinklingReqDto sprinklingReqDto = SprinklingReqDto.builder().amount(20000).people(3).build();

    // When
    final ResultActions actions =
        mockMvc.perform(
            post("/v1/sprinklings")
                .header("X-USER-ID", 900001)
                .header("X-ROOM-ID", "TEST-ROOM")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(sprinklingReqDto)));

    // Then
    actions
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("token").isNotEmpty())
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.receiving").exists())
        .andExpect(jsonPath("_links.read").exists())
        .andExpect(jsonPath("_links.profile").exists())
        .andDo(
            document(
                "sprinkling",
                preprocessResponse(prettyPrint()),
                links(
                    linkWithRel("self").description("셀프 링크"),
                    linkWithRel("receiving").description("받기 링크"),
                    linkWithRel("read").description("조회 링크"),
                    linkWithRel("profile").description("프로파일 링크")),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("Accept 헤더"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type 헤더"),
                    headerWithName("X-USER-ID").description("사용자 ID"),
                    headerWithName("X-ROOM-ID").description("대화방 ID")),
                requestFields(
                    fieldWithPath("amount").description("뿌릴 금액"),
                    fieldWithPath("people").description("뿌릴 인원")),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("Location 헤더"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type 헤더")),
                relaxedResponseFields(fieldWithPath("token").description("뿌리기 token"))));
  }

  @Test
  @DisplayName("요청 Header 누락 테스트")
  void HeaderValidationTest01() throws Exception {

    // Given
    SprinklingReqDto sprinklingReqDto = SprinklingReqDto.builder().amount(20000).people(3).build();

    // When
    final ResultActions actions =
        mockMvc.perform(
            post("/v1/sprinklings")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(sprinklingReqDto)));

    // Then
    actions
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("timestamp").exists())
        .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.name()))
        .andExpect(jsonPath("message").exists())
        .andExpect(jsonPath("debugMessage").exists())
        .andExpect(jsonPath("subErrors").isEmpty());
  }

  @Test
  @DisplayName("요청 Header 값 검증 테스트")
  void HeaderValidationTest02() throws Exception {

    // Given
    SprinklingReqDto sprinklingReqDto = SprinklingReqDto.builder().amount(20000).people(3).build();

    // When
    final ResultActions actions =
        mockMvc.perform(
            post("/v1/sprinklings")
                .header("X-USER-ID", 0)
                .header("X-ROOM-ID", "")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(sprinklingReqDto)));

    // Then
    actions
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("timestamp").exists())
        .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.name()))
        .andExpect(jsonPath("message").exists())
        .andExpect(jsonPath("debugMessage").exists())
        .andExpect(jsonPath("subErrors").isEmpty());
  }

  @Test
  @DisplayName("요청 파라미터 값 검증 테스트")
  void ParameterValidationTest() throws Exception {

    // Given
    SprinklingReqDto sprinklingReqDto = SprinklingReqDto.builder().amount(0).build();

    // When
    final ResultActions actions =
        mockMvc.perform(
            post("/v1/sprinklings")
                .header("X-USER-ID", 900001)
                .header("X-ROOM-ID", "TEST-ROOM")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(sprinklingReqDto)));

    // Then
    actions
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("timestamp").exists())
        .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.name()))
        .andExpect(jsonPath("message").exists())
        .andExpect(jsonPath("debugMessage").isEmpty())
        .andExpect(jsonPath("subErrors").exists());
  }

  @Test
  @DisplayName("조회를 요청하고 뿌리기 상태를 반환 받음")
  void readTest() throws Exception {

    // Given
    long amount = 20000;
    int people = 1;
    int userId = 900001;
    int receivingUserId = 900002;
    String roomId = "TEST-ROOM";
    String token = sprinklingService.sprinkle(amount, people, userId, roomId);
    long receivedAmount = receivingService.receive(token, receivingUserId, roomId);

    // When
    final ResultActions actions =
        mockMvc.perform(
            get("/v1/sprinklings/{token}", token)
                .header("X-USER-ID", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON));

    // Then
    actions
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("createDate").isNotEmpty())
        .andExpect(jsonPath("totalAmount").value(amount))
        .andExpect(jsonPath("receivedAmount").value(receivedAmount))
        .andExpect(jsonPath("receivingDtos[0].amount").value(receivedAmount))
        .andExpect(jsonPath("receivingDtos[0].userId").value(receivingUserId))
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.sprinkling").exists())
        .andExpect(jsonPath("_links.receiving").exists())
        .andExpect(jsonPath("_links.profile").exists())
        .andDo(
            document(
                "read",
                preprocessResponse(prettyPrint()),
                links(
                    linkWithRel("self").description("셀프 링크"),
                    linkWithRel("sprinkling").description("뿌리기 링크"),
                    linkWithRel("receiving").description("받기 링크"),
                    linkWithRel("profile").description("프로파일 링크")),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("Accept 헤더"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type 헤더"),
                    headerWithName("X-USER-ID").description("사용자 ID")),
                pathParameters(parameterWithName("token").description("뿌리기 token")),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type 헤더")),
                relaxedResponseFields(
                    fieldWithPath("createDate").description("뿌린 시각"),
                    fieldWithPath("totalAmount").description("뿌린 금액"),
                    fieldWithPath("receivedAmount").description("받기 완료된 금액"),
                    fieldWithPath("receivingDtos[0].amount").description("받은 금액"),
                    fieldWithPath("receivingDtos[0].userId").description("받은 사용자 ID"))));
  }
}
