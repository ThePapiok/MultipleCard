package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import org.springframework.stereotype.Component;

@Component
public class AccountConverter {
    public Account getEntity(RegisterDTO registerDTO){
        Account account = new Account();
        account.setPassword(registerDTO.getPassword());
        account.setPhone(registerDTO.getPhone());
        return account;
    }
}
