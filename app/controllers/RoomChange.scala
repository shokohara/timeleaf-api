package controllers

sealed trait RoomChange
case object UserId extends RoomChange
case object RoomName extends RoomChange
case object Limit extends RoomChange
case object RoomLocked extends RoomChange
case object RoomAuth extends RoomChange
case object WhitelistChange extends RoomChange
case object BlacklistChange extends RoomChange
case object RoomUsers extends RoomChange
