/*
 * Copyright 2015-2018 the original author or authors.
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

package io.pivotal.strepsirrhini.chaosloris.destroyer;

import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

import static io.pivotal.strepsirrhini.chaosloris.destroyer.FateEngine.Fate.THUMBS_DOWN;
import static io.pivotal.strepsirrhini.chaosloris.destroyer.FateEngine.Fate.THUMBS_UP;

@Component
final class RandomFateEngine implements FateEngine {

    private final Random random;

    @Autowired
    RandomFateEngine(Random random) {
        this.random = random;
    }

    @Override
    public Fate getFate(Chaos chaos) {
        return this.random.nextDouble() < chaos.getProbability() ? THUMBS_DOWN : THUMBS_UP;
    }

}
