package com.twitter.gizzard.shards

import scala.collection.mutable


class ReadOnlyShardFactory[ConcreteShard <: Shard](repo: BasicShardRepository) extends shards.ShardFactory[ConcreteShard] {
  def instantiate(shardInfo: shards.ShardInfo, weight: Int, table: ForwardingTable) = repo.constructor(new ReadOnlyShard(shardInfo, weight, getChildren(table)))
  
  def materialize(shardInfo: shards.ShardInfo) = ()
}

class ReadOnlyShard[ConcreteShard <: Shard]
  (val shardInfo: ShardInfo, val weight: Int, val children: Seq[ConcreteShard])
  extends ReadWriteShard[ConcreteShard] {

  val shard = children.first

  def readOperation[A](address: Option[Address], method: (ConcreteShard => A)) = method(shard)

  def writeOperation[A](address: Option[Address], method: (ConcreteShard => A)) =
    throw new ShardRejectedOperationException("shard is read-only")
}
