/*
 * Copyright 2013 the original author or authors.
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
package org.elasticsearch.hadoop.integration.rest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.elasticsearch.hadoop.cfg.ConfigurationOptions;
import org.elasticsearch.hadoop.rest.Resource;
import org.elasticsearch.hadoop.rest.RestClient;
import org.elasticsearch.hadoop.rest.RestRepository;
import org.elasticsearch.hadoop.serialization.JdkValueWriter;
import org.elasticsearch.hadoop.util.BytesArray;
import org.elasticsearch.hadoop.util.TestSettings;
import org.elasticsearch.hadoop.util.TrackingBytesArray;
import org.junit.Test;

public class RestSaveTest {

    @Test
    public void testBulkWrite() throws Exception {
        TestSettings testSettings = new TestSettings("rest/savebulk");
        //testSettings.setPort(9200)
        testSettings.setProperty(ConfigurationOptions.ES_SERIALIZATION_WRITER_CLASS, JdkValueWriter.class.getName());
        RestRepository client = new RestRepository(testSettings);

        Scanner in = new Scanner(getClass().getResourceAsStream("/artists.dat")).useDelimiter("\\n|\\t");

        Map<String, String> line = new LinkedHashMap<String, String>();

        for (; in.hasNextLine();) {
            // ignore number
            in.next();
            line.put("name", in.next());
            line.put("url", in.next());
            line.put("picture", in.next());
            client.writeToIndex(line);
            line.clear();
        }

        client.close();
    }

    @Test
    public void testEmptyBulkWrite() throws Exception {
        TestSettings testSettings = new TestSettings("rest/emptybulk");
        testSettings.setProperty(ConfigurationOptions.ES_SERIALIZATION_WRITER_CLASS, JdkValueWriter.class.getName());
        RestClient client = new RestRepository(testSettings).getRestClient();
        client.bulk(new Resource(testSettings), new TrackingBytesArray(new BytesArray("{}")));
        client.close();
    }
}
