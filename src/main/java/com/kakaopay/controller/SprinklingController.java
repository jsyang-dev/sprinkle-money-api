package com.kakaopay.controller;

import com.kakaopay.dto.ReadDto;
import com.kakaopay.service.SprinklingService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import static com.kakaopay.dto.SprinklingDto.Request;
import static com.kakaopay.dto.SprinklingDto.Response;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/sprinklings")
@Validated
@RequiredArgsConstructor
public class SprinklingController {

  private final SprinklingService sprinklingService;

  @PostMapping
  public ResponseEntity<Response> sprinkle(
      @RequestHeader("X-USER-ID") @Positive int userId,
      @RequestHeader("X-ROOM-ID") @NotBlank String roomID,
      @RequestBody @Valid Request request) {

    String token =
        sprinklingService.sprinkle(request.getAmount(), request.getPeople(), userId, roomID);

    Response response =
        Response.builder()
            .token(token)
            .build()
            .add(linkTo(SprinklingController.class).withSelfRel())
            .add(
                linkTo(methodOn(ReceivingController.class).receive(token, userId, roomID))
                    .withRel("receiving"))
            .add(linkTo(methodOn(SprinklingController.class).read(token, userId)).withRel("read"))
            .add(Link.of("/docs/index.html#sprinkling").withRel("profile"));

    return ResponseEntity.created(
            linkTo(methodOn(SprinklingController.class).read(token, userId)).toUri())
        .body(response);
  }

  @GetMapping("/{token}")
  public ResponseEntity<ReadDto.SprinklingDto> read(
      @PathVariable String token, @RequestHeader("X-USER-ID") @Positive int userId) {

    ReadDto.SprinklingDto sprinklingDto = sprinklingService.read(token, userId);

    sprinklingDto
        .add(linkTo(methodOn(SprinklingController.class).read(token, userId)).withSelfRel())
        .add(linkTo(SprinklingController.class).withRel("sprinkling"))
        .add(
            linkTo(methodOn(ReceivingController.class).receive(token, userId, "roomId"))
                .withRel("receiving"))
        .add(Link.of("/docs/index.html#read").withRel("profile"));

    return ResponseEntity.ok(sprinklingDto);
  }
}
