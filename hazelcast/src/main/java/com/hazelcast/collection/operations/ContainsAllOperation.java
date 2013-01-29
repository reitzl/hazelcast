package com.hazelcast.collection.operations;

import com.hazelcast.collection.CollectionProxyId;
import com.hazelcast.collection.CollectionRecord;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @ali 1/16/13
 */
public class ContainsAllOperation extends CollectionKeyBasedOperation {

    Set<Data> dataSet;

    public ContainsAllOperation() {
    }

    public ContainsAllOperation(CollectionProxyId proxyId, Data dataKey, Set<Data> dataSet) {
        super(proxyId, dataKey);
        this.dataSet = dataSet;
    }

    public void run() throws Exception {
        Collection<CollectionRecord> coll = getCollection();
        if (coll != null) {
            Collection<CollectionRecord> recordSet = new HashSet<CollectionRecord>(dataSet.size());
            for (Data data : dataSet) {
                recordSet.add(new CollectionRecord(isBinary() ? data : toObject(data)));
            }
            response = coll.containsAll(recordSet);
        }
        response = false;
    }

    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeInt(dataSet.size());
        for (Data data : dataSet) {
            data.writeData(out);
        }
    }

    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readData(in);
        int size = in.readInt();
        dataSet = new HashSet<Data>(size);
        for (int i = 0; i < size; i++) {
            Data data = new Data();
            data.readData(in);
            dataSet.add(data);
        }
    }
}
