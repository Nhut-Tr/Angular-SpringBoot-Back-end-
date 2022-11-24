package com.example.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveCart {
    Long userId;
    Long cartId;
}
