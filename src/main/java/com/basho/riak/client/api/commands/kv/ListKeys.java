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
package com.basho.riak.client.api.commands.kv;

import com.basho.riak.client.api.RiakCommand;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.operations.ListKeysOperation;
import com.basho.riak.client.api.commands.CoreFutureAdapter;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.util.BinaryValue;

import java.util.Iterator;
import java.util.List;

 /*
 * @author Dave Rusek <drusek at basho dot com>
 * @since 2.0
 */
public final class ListKeys extends RiakCommand<ListKeys.Response, Namespace>
{

	private final Namespace namespace;
	private final int timeout;

	ListKeys(Builder builder)
	{
		this.namespace = builder.namespace;
		this.timeout = builder.timeout;
	}

	@Override 
    protected final RiakFuture<ListKeys.Response, Namespace> executeAsync(RiakCluster cluster)
    {
        RiakFuture<ListKeysOperation.Response, Namespace> coreFuture = 
            cluster.execute(buildCoreOperation());
        
        CoreFutureAdapter<ListKeys.Response, Namespace, ListKeysOperation.Response, Namespace> future =
            new CoreFutureAdapter<ListKeys.Response, Namespace, ListKeysOperation.Response, Namespace>(coreFuture)
            {
                @Override
                protected Response convertResponse(ListKeysOperation.Response coreResponse)
                {
                    return new Response(namespace, coreResponse.getKeys());
                }

                @Override
                protected Namespace convertQueryInfo(Namespace coreQueryInfo)
                {
                    return coreQueryInfo;
                }
            };
        coreFuture.addListener(future);
        return future;
    }
    
    private ListKeysOperation buildCoreOperation()
    {
        ListKeysOperation.Builder builder = new ListKeysOperation.Builder(namespace);

		if (timeout > 0)
		{
			builder.withTimeout(timeout);
		}

		return builder.build(); 
    }
    
	public static class Response implements Iterable<Location>
	{

		private final Namespace namespace;
		private final List<BinaryValue> keys;

		public Response(Namespace namespace, List<BinaryValue> keys)
		{
			this.namespace = namespace;
            this.keys = keys;
        }

		@Override
		public Iterator<Location> iterator()
		{
			return new Itr(namespace, keys.iterator());
		}
	}

	private static class Itr implements Iterator<Location>
	{
		private final Iterator<BinaryValue> iterator;
		private final Namespace namespace;

		private Itr(Namespace namespace, Iterator<BinaryValue> iterator)
		{
			this.iterator = iterator;
			this.namespace = namespace;
		}

		@Override
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		@Override
		public Location next()
		{
			BinaryValue key = iterator.next();
			return new Location(namespace, key);
		}

		@Override
		public void remove()
		{
			iterator.remove();
		}
	}

	public static class Builder
	{
		private final Namespace namespace;
		private int timeout;

		public Builder(Namespace namespace)
		{
			if (namespace == null)
            {
                throw new IllegalArgumentException("Namespace cannot be null");
            }
            this.namespace = namespace;
		}

		public Builder withTimeout(int timeout)
		{
			this.timeout = timeout;
			return this;
		}

		public ListKeys build()
		{
			return new ListKeys(this);
		}
	}

}
