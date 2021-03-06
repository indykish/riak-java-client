/*
 * Copyright 2013 Basho Technologies Inc
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
package com.basho.riak.client.api.commands.datatypes;

import com.basho.riak.client.core.query.crdt.ops.SetOp;
import com.basho.riak.client.core.util.BinaryValue;

import java.util.HashSet;
import java.util.Set;

 /*
 * @author Dave Rusek <drusek at basho dot com>
 * @since 2.0
 */
public class SetUpdate implements DatatypeUpdate
{

    private final Set<BinaryValue> adds = new HashSet<BinaryValue>();
    private final Set<BinaryValue> removes = new HashSet<BinaryValue>();

    public SetUpdate()
    {
    }

    public SetUpdate add(BinaryValue value)
    {
        this.adds.add(value);
        return this;
    }

    public SetUpdate add(String value)
    {
        this.adds.add(BinaryValue.create(value));
        return this;
    }
    
    public SetUpdate remove(BinaryValue value)
    {
        this.removes.add(value);
        return this;
    }
    
    public SetUpdate remove(String value)
    {
        this.removes.add(BinaryValue.create(value));
        return this;
    }

    public Set<BinaryValue> getAdds()
    {
        return adds;
    }

    public Set<BinaryValue> getRemoves()
    {
        return removes;
    }

    @Override
    public SetOp getOp()
    {
        return new SetOp(adds, removes);
    }
    
    @Override
    public String toString()
    {
        return "Add: " + adds + " Remove: " + removes;
    }
}
