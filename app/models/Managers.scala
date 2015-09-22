package models

import org.joda.time.DateTime
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue

/**
 * 最後にユーザーが部屋を作成した時間を管理する
 */
object RoomCreateManager {

  private var date = Map.empty[Long, DateTime]

  def get(userId: Long): Option[DateTime] = date.get(userId)

  def update(userId: Long) =
    date = date + (userId -> DateTime.now())

  def remove(userId: Long) =
    date = date - userId
}

/**
 * 最後にユーザーが部屋に入った時間を管理する
 */
object DoorManager {
  private var date = Map.empty[Long, Map[Long, DateTime]]
  private val defaultC = Map.empty[Long, DateTime]

  def get(roomId: Long, userId: Long): Option[DateTime] = {
    date.getOrElse(roomId, defaultC).get(userId)
  }

  def get(roomId: Long): Option[Map[Long, DateTime]] = date.get(roomId)

  def update(roomId: Long, userId: Long) = {
    val seed = date.getOrElse(roomId, defaultC)
    val updated = seed + (userId -> DateTime.now())
    date = date + (roomId -> updated)
  }

  def remove(roomId: Long, userId: Long) {
    val seed = date.getOrElse(roomId, defaultC)
    val updated = seed - userId
    date = date + (roomId -> updated)
  }
}

//object MembersManager {
//  private val default = List.empty[User]
//  private var roomMembers = Map.empty[Long, List[User]]
//  def update(roomId: Long, user: User) {
//    val filteredUsersOfRoomByRoomId = roomMembers.getOrElse(roomId, default)
//    val updated = filteredUsersOfRoomByRoomId :+ user
//    roomMembers = roomMembers + (roomId -> updated)
//  }
//  def remove(roomId: Long, userId: Long) {
//    val filteredUsersOfRoomByRoomId = roomMembers.getOrElse(roomId, default)
//    val updated = filteredUsersOfRoomByRoomId.filter(_.id.isDefined).filterNot(_.id.get == userId)
//    roomMembers = roomMembers + (roomId -> updated)
//  }
//  def exists(roomId: Long, userId: Long) = roomMembers.get(roomId).exists(_ == userId)
//  def get(roomId: Long) = roomMembers.getOrElse(roomId, default)
//}

object MembersManager {
  private val default = List.empty[Long]
  private var roomMembers = Map.empty[Long, List[Long]]
  def update(roomId: Long, userId: Long) {
    val filteredUsersOfRoomByRoomId = roomMembers.getOrElse(roomId, default)
    val updated = filteredUsersOfRoomByRoomId :+ userId
    roomMembers = roomMembers + (roomId -> updated)
  }
  def remove(roomId: Long, userId: Long) {
    val filteredUsersOfRoomByRoomId = roomMembers.getOrElse(roomId, default)
    val updated = filteredUsersOfRoomByRoomId.filterNot(_ == userId)
    roomMembers = roomMembers + (roomId -> updated)
  }
  def exists(roomId: Long, userId: Long) = roomMembers.get(roomId).exists(_ == userId)
  def get(roomId: Long) = roomMembers.getOrElse(roomId, default)
}

object ChannelManager {
  private var channels = Map.empty[Long, Map[Long, Concurrent.Channel[JsValue]]]
  private val defaultC = Map.empty[Long, Concurrent.Channel[JsValue]]

  def updateChannel(roomId: Long, userId: Long, channel: Concurrent.Channel[JsValue]) = {
    val users = channels.getOrElse(roomId, defaultC)
    val updatedUsers = users + (userId -> channel)
    channels = channels + (roomId -> updatedUsers)
  }
  def remove(roomId: Long, userId: Long) = {
    val users = channels.getOrElse(roomId, defaultC)
    val updatedUsers = users - userId
    channels = channels + (roomId -> updatedUsers)
  }
  def exists(roomId: Long, userId: Long): Boolean = channels.get(roomId).exists(_.keys.exists(_ == userId))
  def existingRoomId(userId:Long):Seq[Long]=channels.filter{_._2.keySet.exists(_ == userId)}.map(_._1).toList
  def broadcast(roomId: Long, data: JsValue) = getChannels(roomId).foreach(_.foreach(_._2.push(data)))
  def getChannels(roomId: Long) = channels.get(roomId)
  def unicast(roomId: Long, userId: Long, data: JsValue) = channels.getOrElse(roomId, defaultC).get(userId).foreach(_.push(data))
}
