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

import java.util.List;

/**
 * A repository for handling {@link Chaos}s
 */
@Repository
public interface ChaosRepository extends JpaRepository<Chaos, Long> {

    /**
     * Find all of the {@link Chaos}es related to an {@link Application}
     *
     * @param application the {@link Application} that {@link Chaos}es are related to
     * @return a collection of {@link Chaos}es related to the {@link Application}
     */
    @Transactional(readOnly = true)
    List<Chaos> findByApplication(Application application);

    /**
     * Find all of the {@link Chaos}es related to a {@link Schedule}
     *
     * @param schedule the {@link Schedule} that {@link Chaos}es are related to
     * @return a collection of {@link Chaos}es related to the {@link Schedule}
     */
    @Transactional(readOnly = true)
    List<Chaos> findBySchedule(Schedule schedule);

    /**
     * Find all of the {@link Chaos}es related to a {@link Schedule}
     *
     * @param id the id of {@link Schedule} that {@link Chaos}es are related to
     * @return a collection of {@link Chaos}es related to the {@link Schedule}
     */
    @Transactional(readOnly = true)
    List<Chaos> findByScheduleId(Long id);

}
