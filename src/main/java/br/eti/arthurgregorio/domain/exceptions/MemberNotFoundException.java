package br.eti.arthurgregorio.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseBody
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(UUID memeberExternalId) {
        super("Can't find any member with id [%s]".formatted(memeberExternalId));
    }
}
