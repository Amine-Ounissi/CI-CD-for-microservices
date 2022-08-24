package com.value.authentication.tokenconverter.crypto;

import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;

public interface SignerAndVerifier extends Signer, SignatureVerifier {}
