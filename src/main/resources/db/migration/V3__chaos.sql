-- Copyright 2015 the original author or authors.
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

CREATE TABLE chaos (
  id             INTEGER NOT NULL AUTO_INCREMENT,
  application_id INTEGER NOT NULL,
  probability    FLOAT   NOT NULL,
  schedule_id    INTEGER NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (schedule_id) REFERENCES schedule (id),
  FOREIGN KEY (application_id) REFERENCES application (id),
  UNIQUE (application_id, schedule_id)
);
