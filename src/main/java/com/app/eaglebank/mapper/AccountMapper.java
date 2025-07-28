package com.app.eaglebank.mapper;

import com.app.eaglebank.model.Account;
import com.app.eaglebank.dto.responses.AccountResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public AccountResponse toResponse(Account account) {
        return new AccountResponse(account);
    }
}
