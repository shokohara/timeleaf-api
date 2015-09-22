package controllers

sealed trait Change
case object Name extends Change
case object Bio extends Change
case object Prefecture extends Change
case object Sex extends Change
case object Image extends Change
case object Color extends Change
