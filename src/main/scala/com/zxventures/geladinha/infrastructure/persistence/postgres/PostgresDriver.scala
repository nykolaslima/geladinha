package com.zxventures.geladinha.infrastructure.persistence.postgres

import com.github.tminglei.slickpg._

trait PostgresDriver extends ExPostgresDriver with PgArraySupport with PgJsonSupport with PgEnumSupport with PgPostGISSupport {
  override def pgjson: String = "jsonb"
  override val api = new API with ArrayImplicits with JsonImplicits with PostGISImplicits {}
}

object PostgresDriver extends PostgresDriver
