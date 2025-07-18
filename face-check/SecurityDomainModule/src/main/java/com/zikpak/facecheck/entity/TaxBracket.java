package com.zikpak.facecheck.entity;

import java.math.BigDecimal;

public record TaxBracket(BigDecimal threshold, BigDecimal rate) {

}
