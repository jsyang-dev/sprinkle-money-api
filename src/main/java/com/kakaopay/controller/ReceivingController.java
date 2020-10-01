package com.kakaopay.controller;

import com.kakaopay.dto.ReceivingResDto;
import com.kakaopay.service.ReceivingService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/receivings")
@Validated
@RequiredArgsConstructor
public class ReceivingController {

  private final ReceivingService receivingService;

  @PutMapping("/{token}")
  public ResponseEntity<ReceivingResDto> receive(
      @PathVariable String token,
      @RequestHeader("X-USER-ID") @Positive int userId,
      @RequestHeader("X-ROOM-ID") @NotBlank String roomID) {

    long receivedAmount = receivingService.receive(token, userId, roomID);

    ReceivingResDto receivingResDto =
        ReceivingResDto.builder()
            .amount(receivedAmount)
            .build()
            .add(
                linkTo(methodOn(ReceivingController.class).receive(token, userId, roomID))
                    .withSelfRel())
            .add(linkTo(SprinklingController.class).withRel("sprinkling"))
            //            .add(
            //                linkTo(methodOn(SprinklingController.class).read(token, userId,
            // roomID))
            //                    .withRel("read"))
            .add(Link.of("/docs/index.html#receiving").withRel("profile"));

    return ResponseEntity.ok(receivingResDto);
  }
}
