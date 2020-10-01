package com.kakaopay.controller;

import com.kakaopay.dto.SprinklingReqDto;
import com.kakaopay.dto.SprinklingResDto;
import com.kakaopay.service.SprinklingService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/v1/sprinklings")
@Validated
@RequiredArgsConstructor
public class SprinklingController {

  private final SprinklingService sprinklingService;

  @PostMapping
  public ResponseEntity<SprinklingResDto> distribute(
      @RequestHeader("X-ROOM-ID") @NotBlank String roomID,
      @RequestHeader("X-USER-ID") @Positive int userId,
      @RequestBody @Valid SprinklingReqDto sprinklingReqDto) {

    String token =
        sprinklingService.sprinkle(
            sprinklingReqDto.getAmount(), sprinklingReqDto.getPeople(), userId, roomID);

    SprinklingResDto sprinklingResDto =
        SprinklingResDto.builder()
            .token(token)
            .build()
            .add(linkTo(SprinklingController.class).withSelfRel())
            //                    .add(
            //                            linkTo(methodOn(ReceivingController.class).receive(token,
            // userId, roomID))
            //                                    .withRel("receiving"))
            //                    .add(
            //                            linkTo(
            //                                    methodOn(SprinklingController.class)
            //                                            .read(token, userId, roomID))
            //                                    .withRel("read"))
            .add(Link.of("/docs/index.html#sprinkling").withRel("profile"));

    return ResponseEntity.created(URI.create(""))
        //            linkTo(methodOn(SprinklingController.class).read(token, userId,
        // roomID)).toUri())
        .body(sprinklingResDto);
  }
}
