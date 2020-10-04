package com.kakaopay.controller;

import com.kakaopay.service.SprinklingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriHost = "${app.host}")
@ActiveProfiles("test")
class ReceivingControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private SprinklingService sprinklingService;

  @Test
  @DisplayName("받기를 요청하고 받은 금액을 반환 받음")
  void receiveTest() throws Exception {

    // Given
    long amount = 20000;
    int people = 3;
    int userId = 900001;
    int receivingUserId = 900002;
    String roomId = "TEST-ROOM";
    String token = sprinklingService.sprinkle(amount, people, userId, roomId);

    // When
    final ResultActions actions =
        mockMvc.perform(
            put("/v1/receivings/{token}", token)
                .header("X-USER-ID", receivingUserId)
                .header("X-ROOM-ID", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON));

    // Then
    actions
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("amount").isNumber())
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.sprinkling").exists())
        .andExpect(jsonPath("_links.read").exists())
        .andExpect(jsonPath("_links.profile").exists())
        .andDo(
            document(
                "receiving",
                preprocessResponse(prettyPrint()),
                links(
                    linkWithRel("self").description("셀프 링크"),
                    linkWithRel("sprinkling").description("뿌리기 링크"),
                    linkWithRel("read").description("조회 링크"),
                    linkWithRel("profile").description("프로파일 링크")),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("Accept 헤더"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type 헤더"),
                    headerWithName("X-USER-ID").description("사용자 ID"),
                    headerWithName("X-ROOM-ID").description("대화방 ID")),
                pathParameters(parameterWithName("token").description("뿌리기 token")),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type 헤더")),
                relaxedResponseFields(fieldWithPath("amount").description("받은 금액"))));
  }
}
