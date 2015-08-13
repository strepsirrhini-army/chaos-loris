/*
 * Copyright 2015 the original author or authors.
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

package io.pivotal.strepsirrhini.chaosloris.dashboard;

import io.pivotal.strepsirrhini.chaosloris.model.Instance;
import io.pivotal.strepsirrhini.chaosloris.model.InstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestOperations;

import javax.transaction.Transactional;
import java.util.UUID;

@Controller
final class DashboardController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String api;

    private final InstanceRepository instanceRepository;

    private final RestOperations restOperations;

    @Autowired
    DashboardController(@Value("${ers.apiUri}") String api, InstanceRepository instanceRepository,
                        RestOperations restOperations) {
        this.api = api;
        this.instanceRepository = instanceRepository;
        this.restOperations = restOperations;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.GET, value = "/dashboard/{instanceId}")
    String dashboard(@PathVariable UUID instanceId, Model model) {
        this.logger.info("Displaying dashboard for {}", instanceId);

        Instance instance = this.instanceRepository.getOne(instanceId);
        String result = this.restOperations.getForObject(getOrganizationSummaryUri(instance), String.class);
        this.logger.info("RESULT: {}", result);

        return "dashboard";
    }

    private String getOrganizationSummaryUri(Instance instance) {
        return String.format("%s/v2/organizations/%s/summary", this.api, instance.getOrganizationId());
    }

}
