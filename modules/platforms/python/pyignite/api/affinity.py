# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from typing import Iterable, Union

from pyignite.datatypes import Bool, Int, Long, UUIDObject
from pyignite.datatypes.internal import StructArray
from pyignite.queries import Query, Response
from pyignite.queries.op_codes import OP_CACHE_NODE_PARTITIONS
from pyignite.utils import is_iterable
from .result import APIResult


def cache_get_node_partitions(
    connection: 'Connection',
    caches: Union['Cache', Iterable['Cache']],
    query_id=None,
) -> APIResult:
    """
    Gets partition mapping for an Ignite cache or a number of caches. See
    “IEP-23: Best Effort Affinity for thin clients”.

    :param connection: connection to Ignite server,
    :param caches: Ignite cache or caches the mapping is provided for,
    :param query_id: (optional) a value generated by client and returned as-is
     in response.query_id. When the parameter is omitted, a random value
     is generated,
    :return: API result data object.
    """
    cache_ids = StructArray([
        ('cache_id', Int),
    ])
    query_struct = Query(
        OP_CACHE_NODE_PARTITIONS,
        [
            ('cache_ids', cache_ids),
        ],
        query_id=query_id
    )
    if not is_iterable(caches):
        caches = [caches]
    _, send_buffer = query_struct.from_python({
        'cache_ids': [cache.cache_id for cache in caches],
    })

    connection.send(send_buffer)

    cache_configs = StructArray([
        ('key_type_id', Int),
        ('affinity_key_field_id', Int),
    ])

    node_partitions = StructArray([
        ('partition_id', Int),
    ])

    node_mappings = StructArray([
        ('node_uuid', UUIDObject),
        ('node_partitions', node_partitions)
    ])

    partiton_mappings = StructArray([
        ('is_applicable', Bool),
        ('cache_configs', cache_configs),
        ('node_partitions', node_mappings),
    ])

    response_struct = Response([
        ('version_major', Long),
        ('version_minor', Int),
        ('cache_mapping', partiton_mappings),
    ])
    response_class, recv_buffer = response_struct.parse(connection)
    response = response_class.from_buffer_copy(recv_buffer)

    result = APIResult(response)
    if result.status != 0:
        return result
    # TODO: maybe process mapping here?
    result.value = response_struct.to_python(response)
    return result
