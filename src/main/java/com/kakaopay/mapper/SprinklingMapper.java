package com.kakaopay.mapper;

import com.kakaopay.domain.Sprinkling;
import com.kakaopay.dto.ReadDto.SprinklingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SprinklingMapper {

  @Mapping(source = "amount", target = "totalAmount")
  @Mapping(source = "receivings", target = "receivingDtos")
  SprinklingDto toDto(Sprinkling sprinkling);
}
