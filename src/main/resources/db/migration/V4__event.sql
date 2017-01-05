-- Copyright 2015-2017 the original author or authors.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--      http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

CREATE TABLE event (
  id                   INTEGER  NOT NULL AUTO_INCREMENT,
  chaos_id             INTEGER  NOT NULL,
  executed_at          DATETIME NOT NULL,
  total_instance_count INTEGER  NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (chaos_id) REFERENCES chaos (id)
);

CREATE TABLE event_terminated_instances (
  event_id             INTEGER NOT NULL,
  terminated_instances INTEGER NOT NULL,

  PRIMARY KEY (event_id, terminated_instances)
);
