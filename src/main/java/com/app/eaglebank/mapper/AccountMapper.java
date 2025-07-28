package com.app.eaglebank.mapper;

import com.app.eaglebank.model.Account;
import com.app.eaglebank.dto.responses.AccountResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper component responsible for converting Account entities to API response DTOs.
 *
 * Provides a clean separation between internal data models and external API responses,
 * ensuring only appropriate account information is exposed to clients while maintaining
 * flexibility for future mapping logic enhancements.
 */

@Component
public class AccountMapper {
    public AccountResponse toResponse(Account account) {
        return new AccountResponse(account);
    }
}
