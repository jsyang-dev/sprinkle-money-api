package com.kakaopay.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadDto {

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  public static class SprinklingDto extends RepresentationModel<SprinklingDto>
      implements Serializable {

    private LocalDateTime createDate;

    private Long totalAmount;

    private List<ReceivingDto> receivingDtos;

    public Long getReceivedAmount() {
      return receivingDtos.stream()
          .filter(receivingDto -> Optional.ofNullable(receivingDto.getUserId()).orElse(0) > 0)
          .map(ReceivingDto::getAmount)
          .reduce(0L, Long::sum);
    }
  }

  @Getter
  @Builder
  @AllArgsConstructor
  public static class ReceivingDto implements Serializable {

    private Long amount;

    private Integer userId;
  }
}
