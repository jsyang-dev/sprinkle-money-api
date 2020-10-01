package com.kakaopay.service;

import com.kakaopay.domain.Receiving;
import com.kakaopay.domain.Sprinkling;
import com.kakaopay.exception.InsufficientAmountException;
import com.kakaopay.repository.SprinklingRepository;
import com.kakaopay.util.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SprinklingServiceImpl implements SprinklingService {

  private final SprinklingRepository sprinklingRepository;

  @Override
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
}
