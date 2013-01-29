/*
 * Copyright (c) 2008-2012, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.collection.operations;

import com.hazelcast.collection.CollectionProxyId;
import com.hazelcast.collection.CollectionRecord;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;

import java.util.Collection;

/**
 * @ali 1/16/13
 */
public class RemoveAllOperation extends CollectionBackupAwareOperation {

    transient Collection<CollectionRecord> coll;

    public RemoveAllOperation() {
    }

    public RemoveAllOperation(CollectionProxyId proxyId, Data dataKey, int threadId) {
        super(proxyId, dataKey, threadId);
    }

    public void run() throws Exception {
        coll = removeCollection();
        response = new CollectionResponse(coll, getNodeEngine());
    }

    public void afterRun() throws Exception {
        if (response != null) {
            for (CollectionRecord record : coll) {
                publishEvent(EntryEventType.REMOVED, dataKey, record.getObject());
            }
        }
    }

    public Operation getBackupOperation() {
        return new RemoveAllBackupOperation(proxyId, dataKey);
    }

    public void onWaitExpire() {
        getResponseHandler().sendResponse(new CollectionResponse(null));
    }
}
