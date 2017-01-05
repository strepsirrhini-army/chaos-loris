/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.strepsirrhini.chaosloris.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@ControllerAdvice
final class ExceptionHandlers {

    @ExceptionHandler(DataIntegrityViolationException.class)
    void handleDataIntegrityViolationException(HttpServletResponse response) throws IOException {
        response.sendError(SC_BAD_REQUEST);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    void handleEmptyResultDataAccessException(HttpServletResponse response) throws IOException {
        response.sendError(SC_NOT_FOUND);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    void handleEntityNotFoundException(HttpServletResponse response) throws IOException {
        response.sendError(SC_NOT_FOUND);
    }

}
