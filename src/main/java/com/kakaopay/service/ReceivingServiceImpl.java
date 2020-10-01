package com.kakaopay.service;

import com.kakaopay.domain.Receiving;
import com.kakaopay.domain.Sprinkling;
import com.kakaopay.exception.DuplicateReceivingUserException;
import com.kakaopay.exception.SprinklingNotFoundException;
import com.kakaopay.repository.SprinklingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReceivingServiceImpl implements ReceivingService {

  private final SprinklingRepository sprinklingRepository;

  @Override
  @Transactional
  public long receive(String token, int userId, String roomId) {

    Sprinkling sprinkling =
        sprinklingRepository
            .findByToken(token)
            .orElseThrow(() -> new SprinklingNotFoundException(token));

    if (sprinkling.isReceivingUserDuplicated(userId)) {
      throw new DuplicateReceivingUserException(userId);
    }

    Receiving remainReceiving =
        sprinkling.getReceivings().stream()
            .filter(Receiving::isNotReceived)
            .findFirst()
            .orElseThrow(() -> new SprinklingNotFoundException(token));

    remainReceiving.setUserId(userId);
    return remainReceiving.getAmount();
  }
}
