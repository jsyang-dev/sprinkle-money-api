package com.kakaopay.service;

import com.kakaopay.domain.Receiving;
import com.kakaopay.domain.Sprinkling;
import com.kakaopay.dto.ReadDto;
import com.kakaopay.exception.InsufficientAmountException;
import com.kakaopay.exception.PermissionDeniedException;
import com.kakaopay.exception.ReadExpiredException;
import com.kakaopay.exception.SprinklingNotFoundException;
import com.kakaopay.mapper.SprinklingMapper;
import com.kakaopay.repository.SprinklingRepository;
import com.kakaopay.util.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SprinklingServiceImpl implements SprinklingService {

  private final SprinklingRepository sprinklingRepository;
  private final SprinklingMapper sprinklingMapper;

  @Override
  @Transactional
  public String sprinkle(long amount, int people, int userId, String roomId) {

    if (amount < people) {
      throw new InsufficientAmountException(amount, people);
    }

    Sprinkling sprinkling =
        Sprinkling.builder()
            .token(RandomUtils.generateToken())
            .amount(amount)
            .people(people)
            .roomId(roomId)
            .userId(userId)
            .build();

    makeReceivingInSprinkling(amount, people, sprinkling);

    return sprinklingRepository.save(sprinkling).getToken();
  }

  @Override
  public ReadDto.SprinklingDto read(String token, int userId) {

    Sprinkling sprinkling =
        sprinklingRepository
            .findByToken(token)
            .orElseThrow(() -> new SprinklingNotFoundException(token));

    validateReading(sprinkling, token, userId);

    return sprinklingMapper.toDto(sprinkling);
  }

  private void makeReceivingInSprinkling(long amount, int people, Sprinkling sprinkling) {
    long remainAmount = amount;

    for (int remainPeople = people; remainPeople > 0; remainPeople--) {
      long sprinklingAmount;
      if (remainPeople == 1) {
        sprinklingAmount = remainAmount;
      } else {
        sprinklingAmount = RandomUtils.generateRandomMoney(remainAmount, remainPeople);
      }
      remainAmount -= sprinklingAmount;

      Receiving receiving = Receiving.builder().amount(sprinklingAmount).build();
      sprinkling.addReceiving(receiving);
    }
  }

  private void validateReading(Sprinkling sprinkling, String token, int userId) {
    if (sprinkling.isPermissionDenied(userId)) {
      throw new PermissionDeniedException(token);
    }
    if (sprinkling.isReadExpired()) {
      throw new ReadExpiredException(sprinkling.getCreateDate());
    }
  }
}
