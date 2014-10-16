package com.edifecs.build

import sbt._
import sbt.Keys._

object Directory {
  def apply(path: String): Project = {
    Project(
      id = path,
      base = file(path),
      settings = Seq(
        name := thisProject.value.id,
        baseDirectory := thisProject.value.base
      ),
      auto = AddSettings.seq(AddSettings.buildScalaFiles, AddSettings.defaultSbtFiles)
    )
  }
}
