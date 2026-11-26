package com.receitadevoinha.api.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca um endpoint que exige o cabecalho "Authorization: Bearer &lt;token&gt;".
 * Sem token valido o {@link AuthInterceptor} responde 401 antes do controller rodar.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequerLogin {
}
