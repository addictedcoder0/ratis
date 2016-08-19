/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.raft.hadoopRpc.client;

import org.apache.hadoop.conf.Configuration;
import org.apache.raft.client.RaftClientRequestSender;
import org.apache.raft.hadoopRpc.HadoopRpcBase;
import org.apache.raft.hadoopRpc.HadoopUtils;
import org.apache.raft.protocol.RaftClientReply;
import org.apache.raft.protocol.RaftClientRequest;
import org.apache.raft.protocol.RaftPeer;
import org.apache.raft.protocol.SetConfigurationRequest;

import java.io.IOException;
import java.util.Collection;

public class HadoopClientRequestSender
    extends HadoopRpcBase<RaftClientProtocolClientSideTranslatorPB>
    implements RaftClientRequestSender {
  public HadoopClientRequestSender(
      Collection<RaftPeer> peers, Configuration conf) {
    super(conf);
    addPeers(peers);
  }

  @Override
  public RaftClientReply sendRequest(RaftClientRequest request)
      throws IOException {
    final String serverId = request.getReplierId();
    final RaftClientProtocolClientSideTranslatorPB proxy = getServerProxy(serverId);
    if (request instanceof SetConfigurationRequest) {
      return proxy.setConfiguration((SetConfigurationRequest)request);
    } else {
      return proxy.submitClientRequest(request);
    }
  }

  @Override
  public RaftClientProtocolClientSideTranslatorPB createProxy(RaftPeer p)
      throws IOException {
    final RaftClientProtocolPB proxy = HadoopUtils.getProxy(
        RaftClientProtocolPB.class, p.getAddress(), getConf());
    return new RaftClientProtocolClientSideTranslatorPB(proxy);
  }

  @Override
  public void addServers(Iterable<RaftPeer> servers) {
    addPeers(servers);
  }
}