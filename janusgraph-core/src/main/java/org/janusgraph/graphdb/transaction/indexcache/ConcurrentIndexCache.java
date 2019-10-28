// Copyright 2017 JanusGraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.janusgraph.graphdb.transaction.indexcache;

import com.google.common.collect.HashMultimap;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.JanusGraphVertexProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class ConcurrentIndexCache implements IndexCache {

    private final HashMultimap<Object,JanusGraphVertexProperty> map;
    private final Set<PropertyKey> properties;

    public ConcurrentIndexCache() {
        this.map = HashMultimap.create();
        this.properties = new HashSet<>();
    }

    @Override
    public synchronized void add(JanusGraphVertexProperty property) {
        map.put(property.value(),property);
        properties.add(property.propertyKey());
    }

    @Override
    public synchronized void remove(JanusGraphVertexProperty property) {
        map.remove(property.value(),property);
        properties.remove(property.propertyKey());
    }

    @Override
    public synchronized Iterable<JanusGraphVertexProperty> get(final Object value, final PropertyKey key) {
        final List<JanusGraphVertexProperty> result = new ArrayList<>(4);
        for (JanusGraphVertexProperty p : map.get(value)) {
            if (p.propertyKey().equals(key)) result.add(p);
        }
        return result;
    }

    @Override
    public boolean contains(final PropertyKey property) {
        return properties.contains(property);
    }
}
