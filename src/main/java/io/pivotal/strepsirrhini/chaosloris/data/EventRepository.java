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

package io.pivotal.strepsirrhini.chaosloris.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * A repository for handling {@link Event}s
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Find all of the {@link Event}s related to a {@link Chaos}
     *
     * @param chaos the {@link Chaos} that {@link Event}s are related to
     * @return a collection of {@link Event}s related to the {@link Chaos}
     */
    @Transactional(readOnly = true)
    List<Event> findByChaos(Chaos chaos);

    /**
     * Find all of the {@link Event}s that occurred before an {@link Instant}
     *
     * @param instant the {@link Instant} to find {@link Event}s before
     * @return a collection {@link Event}s that occurred before the {@link Instant}
     */
    @Transactional(readOnly = true)
    List<Event> findByExecutedAtBefore(Instant instant);

}
